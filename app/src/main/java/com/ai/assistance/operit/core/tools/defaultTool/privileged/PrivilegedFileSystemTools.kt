package com.ai.assistance.operit.core.tools.defaultTool.privileged

import android.content.Context
import com.ai.assistance.operit.core.tools.defaultTool.accessbility.AccessibilityFileSystemTools

/**
 * PRIVILEGED 档文件系统工具。
 * 无独立特权面：跨应用私有目录读取属 shell uid 能力（已随三档裁撤），
 * 应用沙箱内文件操作沿用无障碍档实现。
 */
open class PrivilegedFileSystemTools(context: Context) : AccessibilityFileSystemTools(context)
