package com.ozak.app

import android.app.*
import android.content.*
import android.os.Build

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "موعدك الآن"
        val channel = "ozak_alarm"
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            nm.createNotificationChannel(NotificationChannel(channel, "OZAK alarms",
                NotificationManager.IMPORTANCE_HIGH).apply { description = "Appointment alarms" })
        }
        val full = PendingIntent.getActivity(context, 777,
            Intent(context, AlarmActivity::class.java).putExtra("title", title),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification = Notification.Builder(context, channel)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("OZAK — حان الموعد")
            .setContentText(title)
            .setCategory(Notification.CATEGORY_ALARM)
            .setPriority(Notification.PRIORITY_MAX)
            .setFullScreenIntent(full, true)
            .setAutoCancel(true).build()
        nm.notify(title.hashCode(), notification)
        try { context.startActivity(Intent(context, AlarmActivity::class.java)
            .putExtra("title", title).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)) }
        catch (_: Exception) {}
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        AlarmScheduler.restore(context)
    }
}
