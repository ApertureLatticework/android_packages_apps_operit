@file:Suppress("unused")

package com.arthenica.ffmpegkit

/**
 * ffmpeg-kit 同名同包门面（树内专用源，app/src/soong/java 不参与 Gradle 构建）。
 *
 * 包名刻意与 AAR 完全一致：消费方（FFmpegUtil/StandardFFmpegTool/OpenSourceLicenses）
 * 的 `import com.arthenica.ffmpegkit.*` 在 Gradle（AAR 供类）与 Soong（本门面供类）
 * 两个世界零改动通用，不存在翻牌切换步骤。
 * 背后是包内薄壳 liboperit_ffmpeg（JNI 直走 external/ffmpeg 树内源码线），
 * ffmpeg-kit AAR 与 smart-exception 依赖仅在 Gradle 构建中存在。
 */
object FFmpegKit {
    init {
        System.loadLibrary("operit_ffmpeg")
    }

    private external fun nativeExecute(command: String): Int
    private external fun nativeOutput(): String

    /** 执行一条 ffmpeg 命令（空格分词，尊重引号），返回带 returnCode/output 的会话 */
    fun execute(command: String): FFmpegSession {
        val rc = nativeExecute(command)
        return FFmpegSession(ReturnCode(rc), nativeOutput())
    }
}

class FFmpegSession(val returnCode: ReturnCode, val output: String?)

class ReturnCode(val value: Int) {
    companion object {
        fun isSuccess(returnCode: ReturnCode): Boolean = returnCode.value == success
        fun isCancel(returnCode: ReturnCode): Boolean = returnCode.value == cancel
        fun isFailure(returnCode: ReturnCode): Boolean =
            returnCode.value != success && returnCode.value != cancel

        const val success: Int = 0
        const val cancel: Int = 255
    }
}

object FFprobeKit {
    init {
        System.loadLibrary("operit_ffmpeg")
    }

    private external fun nativeProbe(path: String): String?

    /** avformat 直读媒体信息，解析失败返回 mediaInformation 为空的会话 */
    fun getMediaInformation(path: String): MediaInformationSession {
        val json = nativeProbe(path)
        return MediaInformationSession(MediaInformation.parse(json))
    }
}

class MediaInformationSession(val mediaInformation: MediaInformation?)

class MediaInformation(
    val filename: String?,
    val format: String?,
    val duration: String?,
    val bitrate: String?,
    val size: String?,
    val streams: List<StreamInformation>
) {
    companion object {
        /** 薄壳 JNI 的 JSON 形态（org.json 为平台自带，JVM 单测桩除外） */
        fun parse(json: String?): MediaInformation? {
            if (json == null) return null
            return try {
                val obj = org.json.JSONObject(json)
                val streams = obj.optJSONArray("streams") ?: return MediaInformation(
                    filename = obj.optString("filename"),
                    format = obj.optString("format"),
                    duration = obj.optString("duration"),
                    bitrate = obj.optString("bitrate"),
                    size = obj.optString("size"),
                    streams = emptyList()
                )
                val list = (0 until streams.length()).map { i ->
                    val s = streams.getJSONObject(i)
                    StreamInformation(
                        index = s.optString("index"),
                        type = s.optString("type"),
                        codec = s.optString("codec"),
                        width = s.optInt("width"),
                        height = s.optInt("height"),
                        sampleRate = s.optString("sample_rate"),
                        channels = s.optInt("channels"),
                        // 消费面实测只读 r_frame_rate（StandardFFmpegTool 帧率对齐）
                        allProperties = if (s.has("r_frame_rate")) {
                            mapOf("r_frame_rate" to s.optString("r_frame_rate"))
                        } else {
                            null
                        }
                    )
                }
                MediaInformation(
                    filename = obj.optString("filename"),
                    format = obj.optString("format"),
                    duration = obj.optString("duration"),
                    bitrate = obj.optString("bitrate"),
                    size = obj.optString("size"),
                    streams = list
                )
            } catch (e: org.json.JSONException) {
                android.util.Log.w("FFprobeKit", "probe json parse failed: ${e.message}")
                null
            }
        }
    }
}

class StreamInformation(
    val index: String?,
    val type: String?,
    val codec: String?,
    val width: Int,
    val height: Int,
    val sampleRate: String?,
    val channels: Int,
    val allProperties: Map<String, String?>?
)

object FFmpegKitConfig {
    init {
        System.loadLibrary("operit_ffmpeg")
    }

    private external fun nativeVersion(): String

    /** av_version_info（libavutil），如 "n8.0-xxx" */
    fun getVersion(): String = nativeVersion()

    fun getBuildDate(): String = "in-tree"
}
