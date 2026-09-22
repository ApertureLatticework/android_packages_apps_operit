/**
 * Operit ffmpeg 薄壳 JNI（步骤 5 修订后的源码线）。
 *
 * 为什么存在：ffmpeg-kit 上游已归档且与 ffmpeg 8.0 存在 API 漂移，不值得移植；
 * 本应用对 ffmpeg 的全部消费面只有 FFmpegKit.execute / FFprobeKit.getMediaInformation /
 * FFmpegKitConfig 版本串三件（rg 实测 6 函数），自写薄壳即可整替。
 *
 * 机制：
 * - execute：经 fork 侧 libffmpeg_cli 模块（fftools 以 -Dmain=ffmpeg_cli_main 编成库）
 *   跑完整 CLI，av_log 回调收集输出等价 session.output；ffmpeg CLI 使用全局状态，
 *   互斥锁串行化并发调用
 * - probe：avformat 直读（时长/码率/尺寸/流信息），手拼 JSON 给 Kotlin 门面解析
 *
 * Gradle 过渡期：Gradle 构建不编译本文件，app 侧继续消费 ffmpeg-kit AAR；
 * Android.bp.tree 翻牌时随 liboperit_ffmpeg 模块进树内构建。
 */
#include <jni.h>
#include <pthread.h>
#include <stdarg.h>
#include <cstdio>
#include <string>
#include <vector>

#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavutil/avutil.h>
#include <libavutil/log.h>

extern "C" {
// fork 侧 fftools 模块（libffmpeg_cli）经 -Dmain=ffmpeg_cli_main 暴露的 CLI 入口
int ffmpeg_cli_main(int argc, char **argv);
}

static pthread_mutex_t g_ffmpeg_mutex = PTHREAD_MUTEX_INITIALIZER;
static std::string g_log_buffer;
static std::string g_last_output;

static void operit_log_callback(void *ptr, int level, const char *fmt, va_list vl) {
    if (level > av_log_get_level()) {
        return;
    }
    char line[1024];
    int n = vsnprintf(line, sizeof(line), fmt, vl);
    if (n > 0) {
        g_log_buffer += line;
    }
}

/** 按空格分词，尊重单双引号（等价 ffmpeg-kit 的参数分词规则） */
static std::vector<std::string> split_args(const std::string &cmd) {
    std::vector<std::string> out;
    std::string cur;
    bool in_single = false;
    bool in_double = false;
    for (char c : cmd) {
        if (c == '\'' && !in_double) {
            in_single = !in_single;
            continue;
        }
        if (c == '"' && !in_single) {
            in_double = !in_double;
            continue;
        }
        if ((c == ' ' || c == '\t') && !in_single && !in_double) {
            if (!cur.empty()) {
                out.push_back(cur);
                cur.clear();
            }
            continue;
        }
        cur += c;
    }
    if (!cur.empty()) {
        out.push_back(cur);
    }
    return out;
}

// 本库独占进程内的 ffmpeg 日志通道：装载时装一次，永不还原。
// （ffmpeg 无 av_log_get_callback；此前的存取还原写法建立在虚构 API 上）
extern "C" JNIEXPORT jint JNI_OnLoad(JavaVM *, void *) {
    av_log_set_callback(operit_log_callback);
    return JNI_VERSION_1_6;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_ai_assistance_operit_util_ffmpeg_FFmpegKit_nativeExecute(JNIEnv *env, jclass, jstring command) {
    const char *cmdUtf = env->GetStringUTFChars(command, nullptr);
    std::vector<std::string> args = split_args(cmdUtf);
    env->ReleaseStringUTFChars(command, cmdUtf);

    std::vector<char *> argv;
    argv.reserve(args.size() + 1);
    argv.push_back(const_cast<char *>("ffmpeg"));
    for (auto &a : args) {
        argv.push_back(const_cast<char *>(a.c_str()));
    }

    pthread_mutex_lock(&g_ffmpeg_mutex);
    g_log_buffer.clear();
    int rc = ffmpeg_cli_main(static_cast<int>(argv.size()), argv.data());
    g_last_output = g_log_buffer;
    pthread_mutex_unlock(&g_ffmpeg_mutex);
    return rc;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_ai_assistance_operit_util_ffmpeg_FFmpegKit_nativeOutput(JNIEnv *env, jclass) {
    pthread_mutex_lock(&g_ffmpeg_mutex);
    std::string copy = g_last_output;
    pthread_mutex_unlock(&g_ffmpeg_mutex);
    return env->NewStringUTF(copy.c_str());
}

static void json_escape_into(std::string &out, const char *v) {
    if (v == nullptr) {
        return;
    }
    for (const char *p = v; *p; p++) {
        switch (*p) {
            case '"': out += "\\\""; break;
            case '\\': out += "\\\\"; break;
            case '\n': out += "\\n"; break;
            case '\r': out += "\\r"; break;
            case '\t': out += "\\t"; break;
            default: out += *p;
        }
    }
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_ai_assistance_operit_util_ffmpeg_FFprobeKit_nativeProbe(JNIEnv *env, jclass, jstring path) {
    const char *pathUtf = env->GetStringUTFChars(path, nullptr);
    AVFormatContext *ctx = nullptr;
    if (avformat_open_input(&ctx, pathUtf, nullptr, nullptr) < 0) {
        env->ReleaseStringUTFChars(path, pathUtf);
        return nullptr;
    }
    env->ReleaseStringUTFChars(path, pathUtf);
    if (avformat_find_stream_info(ctx, nullptr) < 0) {
        avformat_close_input(&ctx);
        return nullptr;
    }

    std::string json;
    json += "{\"filename\":\"";
    json_escape_into(json, ctx->url ? ctx->url : "");
    json += "\",\"format\":\"";
    json_escape_into(json, ctx->iformat ? ctx->iformat->name : "");
    char buf[512];
    snprintf(buf, sizeof(buf),
             "\",\"duration\":\"%.6f\",\"bitrate\":\"%lld\",\"size\":\"%lld\",\"streams\":[",
             ctx->duration != AV_NOPTS_VALUE ? ctx->duration / (double) AV_TIME_BASE : 0.0,
             (long long) ctx->bit_rate,
             (long long) (ctx->pb ? avio_size(ctx->pb) : -1));
    json += buf;

    for (unsigned i = 0; i < ctx->nb_streams; i++) {
        AVStream *st = ctx->streams[i];
        AVCodecParameters *cp = st->codecpar;
        const char *type = "other";
        if (cp->codec_type == AVMEDIA_TYPE_VIDEO) {
            type = "video";
        } else if (cp->codec_type == AVMEDIA_TYPE_AUDIO) {
            type = "audio";
        }
        snprintf(buf, sizeof(buf),
                 "%s{\"index\":\"%d\",\"type\":\"%s\",\"codec\":\"%s\",\"width\":%d,\"height\":%d,"
                 "\"sample_rate\":\"%d\",\"channels\":%d}",
                 i ? "," : "", st->index, type, avcodec_get_name(cp->codec_id),
                 cp->width, cp->height, cp->sample_rate, cp->ch_layout.nb_channels);
        json += buf;
    }
    json += "]}";

    avformat_close_input(&ctx);
    return env->NewStringUTF(json.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_ai_assistance_operit_util_ffmpeg_FFmpegKitConfig_nativeVersion(JNIEnv *env, jclass) {
    return env->NewStringUTF(av_version_info());
}
