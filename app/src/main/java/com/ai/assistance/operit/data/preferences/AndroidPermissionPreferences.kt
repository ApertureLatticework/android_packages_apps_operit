package com.ai.assistance.operit.data.preferences

import android.content.Context
import com.ai.assistance.operit.util.AppLogger
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ai.assistance.operit.core.tools.system.AndroidPermissionLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.androidPermissionDataStore: DataStore<Preferences> by
        preferencesDataStore(name = "android_permission_preferences")

/** 全局单例实例 */
lateinit var androidPermissionPreferences: AndroidPermissionPreferences
    private set

private val androidPermissionPreferencesInitLock = Any()
@Volatile
private var androidPermissionPreferencesInitialized = false

/** 初始化Android权限偏好管理器。该入口可被多个进程组件重复调用。 */
fun initAndroidPermissionPreferences(context: Context) {
    if (androidPermissionPreferencesInitialized) {
        return
    }
    synchronized(androidPermissionPreferencesInitLock) {
        if (androidPermissionPreferencesInitialized) {
            return
        }
        androidPermissionPreferences = AndroidPermissionPreferences(context)
        androidPermissionPreferencesInitialized = true
    }
}

/** Android权限偏好管理器 负责管理应用全局的权限级别偏好设置 */
class AndroidPermissionPreferences(private val context: Context) {
    companion object {
        private const val TAG = "AndroidPermissionPrefs"

        // 权限相关键
        private val PREFERRED_PERMISSION_LEVEL = stringPreferencesKey("preferred_permission_level")
    }

    /**
     * 检查是否已设置权限级别
     * @return 是否已设置权限级别
     */
    fun isPermissionLevelSet(): Boolean {
        return runBlocking {
            try {
                preferredPermissionLevelFlow.first() != null
            } catch (e: Exception) {
                AppLogger.e(TAG, "Error checking if permission level is set", e)
                false
            }
        }
    }

}
