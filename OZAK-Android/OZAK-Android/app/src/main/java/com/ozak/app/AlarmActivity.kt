package com.ozak.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import android.graphics.Typeface

class AlarmActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags( android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val title = intent.getStringExtra("title") ?: "موعدك الآن"
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32); setBackgroundColor(Color.rgb(10,10,12))
        }
        val heading = TextView(this).apply {
            text = "OZAK"; textSize = 30f; setTextColor(Color.rgb(212,175,55))
            gravity = Gravity.CENTER; typeface = Typeface.SERIF
        }
        val msg = TextView(this).apply {
            text = "حان الموعد\n$title"; textSize = 25f; setTextColor(Color.WHITE)
            gravity = Gravity.CENTER; setPadding(0,40,0,40)
        }
        val done = Button(this).apply { text = "إيقاف المنبه"; setOnClickListener { finish() } }
        layout.addView(heading); layout.addView(msg); layout.addView(done)
        setContentView(layout)
    }
}
