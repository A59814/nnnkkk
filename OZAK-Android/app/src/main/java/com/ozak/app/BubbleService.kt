package com.ozak.app

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.provider.Settings
import android.view.*
import android.widget.TextView
import android.graphics.Color
import android.graphics.Typeface

class BubbleService : Service() {
    private var windowManager: WindowManager? = null
    private var bubble: TextView? = null

    override fun onCreate() {
        super.onCreate()
        if (!Settings.canDrawOverlays(this)) { stopSelf(); return }
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        bubble = TextView(this).apply {
            text = "O"; textSize = 23f; gravity = Gravity.CENTER
            setTextColor(Color.rgb(212,175,55)); typeface = Typeface.SERIF
            background = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setColor(Color.argb(225, 20,20,25))
                setStroke(2, Color.rgb(212,175,55))
            }
        }
        val type = if (android.os.Build.VERSION.SDK_INT >= 26)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else WindowManager.LayoutParams.TYPE_PHONE
        val params = WindowManager.LayoutParams(58,58,type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT).apply {
            gravity = Gravity.TOP or Gravity.START; x = 24; y = 220
        }
        var sx=0f; var sy=0f; var ox=0; var oy=0; var moved=false
        bubble?.setOnTouchListener { v, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> { sx=event.rawX; sy=event.rawY; ox=params.x; oy=params.y; moved=false; true }
                MotionEvent.ACTION_MOVE -> {
                    val dx=event.rawX-sx; val dy=event.rawY-sy
                    if (kotlin.math.abs(dx)>6 || kotlin.math.abs(dy)>6) moved=true
                    params.x=ox+dx.toInt(); params.y=oy+dy.toInt()
                    windowManager?.updateViewLayout(v,params); true
                }
                MotionEvent.ACTION_UP -> {
                    if (!moved) {
                        val launch=packageManager.getLaunchIntentForPackage(packageName)
                        launch?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        startActivity(launch)
                    }
                    true
                }
                else -> false
            }
        }
        windowManager?.addView(bubble,params)
    }
    override fun onDestroy() {
        bubble?.let { try { windowManager?.removeView(it) } catch (_: Exception) {} }
        bubble=null; super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? = null
}
