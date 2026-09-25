package com.ai.assistance.operit.ui.features.chat.webview

import android.content.Context
import com.ai.assistance.operit.R
import com.ai.assistance.operit.util.OperitPaths
import java.io.File
import java.io.IOException

fun createAndGetDefaultWorkspace(context: Context, chatId: String): File {
    return createAndGetDefaultWorkspace(context, chatId, null)
}

fun createAndResetWorkspaceDirectory(context: Context, chatId: String): File {
    val workspaceDir = File(getWorkspacePath(context, chatId))
    if (workspaceDir.exists()) {
        workspaceDir.deleteRecursively()
    }
    workspaceDir.mkdirs()
    return workspaceDir
}

fun createAndGetDefaultWorkspace(context: Context, chatId: String, projectType: String?): File {
    // 创建内部存储工作区
    val workspacePath = getWorkspacePath(context, chatId)
    ensureWorkspaceDirExists(workspacePath)

    val webContentDir = File(workspacePath)

    // 根据项目类型复制模板文件并创建配置
    when (projectType) {
        "office" -> {
            copyTemplateFiles(context, webContentDir, "office")
            createProjectConfigIfNeeded(context, webContentDir, ProjectType.OFFICE)
        }
        "blank" -> {
            createProjectConfigIfNeeded(context, webContentDir, ProjectType.BLANK)
        }
        else -> {
            copyTemplateFiles(context, webContentDir, "web")
            createProjectConfigIfNeeded(context, webContentDir, ProjectType.WEB)
        }
    }

    return webContentDir
}

/**
 * 获取工作区路径（新位置：内部存储）
 * 路径: /data/data/com.ai.assistance.operit/files/workspace/{chatId}
 */
fun getWorkspacePath(context: Context, chatId: String): String {
    return File(context.filesDir, "workspace/$chatId").absolutePath
}

/**
 * 获取旧的工作区路径（外部存储）
 * 路径: /sdcard/Download/Operit/workspace/{chatId}
 */
fun getLegacyWorkspacePath(chatId: String): String {
    return OperitPaths.workspacePathSdcard(chatId)
}

fun ensureWorkspaceDirExists(path: String): File {
    val workspaceDir = File(path)
    if (!workspaceDir.exists()) {
        workspaceDir.mkdirs()
    }
    return workspaceDir
}

private enum class ProjectType {
    WEB, OFFICE, BLANK
}

/**
 * 生成空白项目配置JSON
 */
private fun generateBlankProjectConfig(context: Context): String {
    return """
{
    "projectType": "blank",
    "title": "${context.getString(R.string.workspace_project_blank_title)}",
    "description": "${context.getString(R.string.workspace_project_blank_description)}",
    "server": {
        "enabled": false,
        "port": 8080,
        "autoStart": false
    },
    "preview": {
        "type": "terminal",
        "url": "",
        "showPreviewButton": false,
        "previewButtonLabel": ""
    },
    "commands": []
    
}
""".trimIndent()
}

/**
 * 生成Web项目配置JSON
 */
private fun generateWebProjectConfig(context: Context): String {
    return """
{
    "projectType": "web",
    "title": "${context.getString(R.string.workspace_project_web_title)}",
    "description": "${context.getString(R.string.workspace_project_web_description)}",
    "server": {
        "enabled": true,
        "port": 8093,
        "autoStart": true
    },
    "preview": {
        "type": "browser",
        "url": "http://localhost:8093"
    },
    "commands": []
}
""".trimIndent()
}

/**
 * 生成办公文档项目配置JSON
 */
private fun generateOfficeProjectConfig(context: Context): String {
    return """
{
    "projectType": "office",
    "title": "${context.getString(R.string.workspace_project_office_title)}",
    "description": "${context.getString(R.string.workspace_project_office_description)}",
    "server": {
        "enabled": false,
        "port": 8080,
        "autoStart": false
    },
    "preview": {
        "type": "terminal",
        "url": "",
        "showPreviewButton": false,
        "previewButtonLabel": ""
    },
    "commands": []
    
}
""".trimIndent()
}

/**
 * 从 assets 复制项目模板文件到工作区
 */
private fun copyTemplateFiles(context: Context, workspaceDir: File, templateName: String) {
    val assetManager = context.assets
    val templatePath = "templates/$templateName"

    try {
        val files = assetManager.list(templatePath) ?: return

        for (filename in files) {
            val sourcePath = "$templatePath/$filename"
            // 特殊处理：gitignore (无点) -> .gitignore (有点)
            // 因为 Android 构建工具会排除 assets 中的 .gitignore 文件
            val destFileName = if (filename == "gitignore") ".gitignore" else filename
            val destFile = File(workspaceDir, destFileName)

            // 检查是否是目录
            val isDirectory = try {
                assetManager.list(sourcePath)?.isNotEmpty() == true
            } catch (e: IOException) {
                false
            }

            if (isDirectory) {
                // 递归复制子目录
                destFile.mkdirs()
                copyTemplateFilesRecursive(assetManager, sourcePath, destFile)
            } else {
                // 复制文件
                assetManager.open(sourcePath).use { inputStream ->
                    destFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

/**
 * 递归复制模板文件
 */
private fun copyTemplateFilesRecursive(assetManager: android.content.res.AssetManager, sourcePath: String, destDir: File) {
    try {
        val files = assetManager.list(sourcePath) ?: return

        for (filename in files) {
            val currentSourcePath = "$sourcePath/$filename"
            val destFile = File(destDir, filename)

            val isDirectory = try {
                assetManager.list(currentSourcePath)?.isNotEmpty() == true
            } catch (e: IOException) {
                false
            }

            if (isDirectory) {
                destFile.mkdirs()
                copyTemplateFilesRecursive(assetManager, currentSourcePath, destFile)
            } else {
                assetManager.open(currentSourcePath).use { inputStream ->
                    destFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

private fun createProjectConfigIfNeeded(context: Context, workspaceDir: File, projectType: ProjectType) {
    // 创建 .operit 目录和 config.json
    val operitDir = File(workspaceDir, ".operit")
    if (!operitDir.exists()) {
        operitDir.mkdirs()
    }

    val configFile = File(operitDir, "config.json")
    if (configFile.exists()) {
        return
    }

    val configContent = when (projectType) {
        ProjectType.WEB -> generateWebProjectConfig(context)
        ProjectType.OFFICE -> generateOfficeProjectConfig(context)
        ProjectType.BLANK -> generateBlankProjectConfig(context)
    }

    try {
        configFile.writeText(configContent.trimIndent())
    } catch (_: IOException) {
        // Ignore write errors for now
    }
}
