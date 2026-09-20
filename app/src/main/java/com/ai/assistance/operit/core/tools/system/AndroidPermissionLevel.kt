package com.ai.assistance.operit.core.tools.system

/**
 * 定义工具权限的三层级（ROM 独占，借权档位已整线裁撤）
 * - STANDARD: 基础权限，不需要特殊权限
 * - ACCESSIBILITY: 需要无障碍服务的权限（UI 节点树读取通道）
 * - PRIVILEGED: platform 签名特权，SystemApi 直调（注入/安装/强制停止/安全设置写）
 */
enum class AndroidPermissionLevel {
    STANDARD,      // 普通应用权限
    ACCESSIBILITY, // 无障碍服务权限
    PRIVILEGED;    // 平台签名特权（priv-app 白名单授权）

    companion object {
        /**
         * 从字符串转换为权限等级
         * @param value 权限等级字符串
         * @return 对应的权限等级，如果无法识别则默认为STANDARD
         */
        fun fromString(value: String?): AndroidPermissionLevel {
            return when (value?.uppercase()) {
                "STANDARD" -> STANDARD
                "ACCESSIBILITY" -> ACCESSIBILITY
                "PRIVILEGED" -> PRIVILEGED
                else -> STANDARD // 默认为最低权限
            }
        }
    }
}
