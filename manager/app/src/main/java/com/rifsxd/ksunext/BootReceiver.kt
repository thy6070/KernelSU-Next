package com.rifsxd.ksunext

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Re-applies the toggles the user left enabled, because service.d scripts are not
 * guaranteed to run on every ROM.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        val prefs = context.getSharedPreferences("yx_mode", 0)
        val perf = prefs.getBoolean("perf", false)
        val open = prefs.getBoolean("open", false)
        if (!perf && !open) return

        val bin = suBin()
        Thread {
            runCatching {
                Thread.sleep(8000)
                if (perf) {
                    ProcessBuilder(bin, "-c", "sh /data/adb/service.d/xy_perf.sh")
                        .redirectErrorStream(true).start().waitFor()
                }
                if (open) {
                    ProcessBuilder(bin, "-c", "sh /data/adb/service.d/xy_open_access.sh")
                        .redirectErrorStream(true).start().waitFor()
                }
            }
        }.start()
    }

    private fun suBin(): String {
        for (c in listOf("/data/adb/ksu/bin/su", "/data/adb/magisk/su", "/system/bin/su", "/system/xbin/su")) {
            try {
                if (java.io.File(c).exists()) return c
            } catch (t: Throwable) {
            }
        }
        return "su"
    }
}
