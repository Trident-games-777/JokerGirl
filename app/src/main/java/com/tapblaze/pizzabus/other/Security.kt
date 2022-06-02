package com.tapblaze.pizzabus.other

import android.content.ContentResolver
import android.content.res.Resources
import android.provider.Settings
import java.io.File

class Security(
    private val resources: Resources,
    private val resolver: ContentResolver
) {
    fun isSecurityEnabled(): Boolean {
        return isPathsExist() || adbCode() == "1"
    }

    private fun isPathsExist(): Boolean {
        val pathSet: Set<String> = setOf(
            "/sbin/",
            "/system/bin/",
            "/system/xbin/",
            "/data/local/xbin/",
            "/data/local/bin/",
            "/system/sd/xbin/",
            "/system/bin/failsafe/",
            "/data/local/"
        )
        try {
            for (dir in pathSet) {
                if (File(dir + "su").exists()) return true
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
        return false
    }

    private fun adbCode(): String =
        Settings.Global.getString(resolver, Settings.Global.ADB_ENABLED)
            ?: "null"
}