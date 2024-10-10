package com.example.wetherforcastapp.ui.alarm

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetherforcastapp.MainActivity
import com.example.wetherforcastapp.ui.alarm.AlarmReceiver
import com.example.wetherforcastapp.ui.alarm.viewmodel.AlarmViewModel
import com.example.wetherforcastapp.ui.alarm.viewmodel.AlarmViewModelFactory
import com.example.wetherforcastapp.model.data.database.intyty.EntityAlarm
import com.example.wetherforcastapp.model.data.RepoImpl
import com.example.wetherforcastapp.model.data.UIState
import com.example.wetherforcastapp.model.data.database.LocalDataBaseImp
import com.example.wetherforcastapp.model.data.network.IRemoteDataSourceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*
import kotlin.properties.Delegates

class AlarmService : Service() {
    private lateinit var repo: RepoImpl
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private lateinit var alarmDes: String
    private var sound by Delegates.notNull<Boolean>()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "CANCEL_ALARM") {
            Log.d("AlarmService", "Cancel alarm action received")
            cancelAlarm()
            return START_NOT_STICKY
        }
       repo= RepoImpl.getInstance(
            IRemoteDataSourceImpl.getInstance(),
            LocalDataBaseImp.getInstance(applicationContext)
        )


        serviceScope.launch {
            repo.getAlarmByTime(getCurrentTime()).collect { alarm ->
                sound = alarm.alarm

                repo.getCurrentWeatherLocal().collect {
                    alarmDes = it.currentWeatherResponses.weather[0].description
                    val notification = createNotification("Alarm", alarmDes)
                    startForeground(1, notification)
                    repo.deleteByTime(getCurrentTime())
                }
            }
        }

        createNotificationChannel()
        stopSelfDelayed()
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "alarm_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Alarm Channel", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Channel for alarm notifications"
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(title: String, message: String): Notification {
        val channelId = "alarm_channel"

        val contentIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val contentPendingIntent = PendingIntent.getActivity(
            this, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val cancelIntent = Intent(this, AlarmService::class.java).apply {
            action = "CANCEL_ALARM"
        }
        val cancelPendingIntent = PendingIntent.getService(
            this, 1, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(contentPendingIntent)
            .setDeleteIntent(cancelPendingIntent)
            .setSilent(sound)
            .setOngoing(true)
            .setAutoCancel(false)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Set Off",
                cancelPendingIntent
            )
            .build()
    }


    private fun stopSelfDelayed() {
        val delayMillis = 60 * 1000L // Delay for 1 minute
        Handler().postDelayed({
            stopForeground(true)
            stopSelf()
        }, delayMillis)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun cancelAlarm() {

        stopForeground(true)
        stopSelf()
    }


    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
        val formattedHour = if (hour == 0) 12 else hour

        return String.format("%02d:%02d %s", formattedHour, minute, amPm)
    }
}