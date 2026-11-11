package com.ozak.app

import android.app.*
import android.content.*
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.webkit.*
import android.widget.Toast
import androidx.activity.ComponentActivity
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    private lateinit var web: WebView
    private var backPressedAt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestRuntimePermissions()
        web = WebView(this)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.mediaPlaybackRequiresUserGesture = false
        web.webChromeClient = WebChromeClient()
        web.addJavascriptInterface(AndroidBridge(this), "AndroidBridge")
        setContentView(web)
        web.loadUrl("file:///android_asset/index.html")
    }

    private fun requestRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
        }
        if (Build.VERSION.SDK_INT >= 31 && !getSystemService(AlarmManager::class.java).canScheduleExactAlarms()) {
            startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                Uri.parse("package:$packageName")))
        }
    }

    fun enableBubble() {
        if (!Settings.canDrawOverlays(this)) {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")))
            Toast.makeText(this, "اسمح لـ OZAK بالظهور فوق التطبيقات", Toast.LENGTH_LONG).show()
            return
        }
        startService(Intent(this, BubbleService::class.java))
    }

    fun disableBubble() { stopService(Intent(this, BubbleService::class.java)) }

    override fun onBackPressed() {
        val now = System.currentTimeMillis()
        if (now - backPressedAt < 2000) {
            super.onBackPressed()
            finish()
        } else {
            backPressedAt = now
            Toast.makeText(this, "اضغط مرة أخرى للخروج من OZAK", Toast.LENGTH_SHORT).show()
        }
    }
}

class AndroidBridge(private val activity: MainActivity) {
    @JavascriptInterface fun scheduleAlarm(id: String, time: Long, title: String) {
        AlarmScheduler.schedule(activity, id, time, title)
    }
    @JavascriptInterface fun cancelAlarm(id: String) { AlarmScheduler.cancel(activity, id) }
    @JavascriptInterface fun showBubble() { activity.runOnUiThread { activity.enableBubble() } }
    @JavascriptInterface fun hideBubble() { activity.runOnUiThread { activity.disableBubble() } }
}
