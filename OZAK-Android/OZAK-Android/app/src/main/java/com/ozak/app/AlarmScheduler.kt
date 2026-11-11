package com.ozak.app

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build

object AlarmScheduler {
    fun schedule(context: Context, id: String, time: Long, title: String) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("id", id); putExtra("title", title)
        }
        val pending = PendingIntent.getBroadcast(context, id.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val infoIntent = PendingIntent.getActivity(context, 0,
            context.packageManager.getLaunchIntentForPackage(context.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val info = AlarmManager.AlarmClockInfo(time, infoIntent)
        if (Build.VERSION.SDK_INT >= 31 && !manager.canScheduleExactAlarms()) {
            manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending)
        } else {
            manager.setAlarmClock(info, pending)
        }
        val prefs = context.getSharedPreferences("ozak_alarms", Context.MODE_PRIVATE)
        prefs.edit().putString(id, "$time|$title").apply()
    }

    fun cancel(context: Context, id: String) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pending = PendingIntent.getBroadcast(context, id.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pending)
        context.getSharedPreferences("ozak_alarms", Context.MODE_PRIVATE).edit().remove(id).apply()
    }

    fun restore(context: Context) {
        val prefs = context.getSharedPreferences("ozak_alarms", Context.MODE_PRIVATE)
        prefs.all.forEach { (id, value) ->
            val parts = (value as? String)?.split("|", limit = 2) ?: return@forEach
            val time = parts.getOrNull(0)?.toLongOrNull() ?: return@forEach
            val title = parts.getOrNull(1) ?: "OZAK"
            if (time > System.currentTimeMillis()) schedule(context, id, time, title)
            else prefs.edit().remove(id).apply()
        }
    }
}
