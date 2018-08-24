package com.blackfilter

import android.content.Context.NOTIFICATION_SERVICE
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.view.ViewCompat.setAlpha
import android.widget.SeekBar
import android.graphics.drawable.ColorDrawable
import android.os.SharedMemory
//import org.omg.PortableInterceptor.ACTIVE
import android.os.Bundle
import android.widget.SeekBar.OnSeekBarChangeListener
import android.app.Activity
import android.content.Context
import android.support.v4.app.TaskStackBuilder
import android.view.View


class MainActivity : Activity(), OnSeekBarChangeListener {

    lateinit var shared: com.blackfilter.SharedMemory

    lateinit var alphaSeek: SeekBar
    lateinit var redSeek: SeekBar
    lateinit var greenSeek: SeekBar
    lateinit var blueSeek: SeekBar

    var alpha: Int = 0
    var red: Int = 0
    var green: Int = 0
    var blue: Int = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialize()
    }

    private fun initialize() {

        stopServiceIfActive()

        shared = SharedMemory(this)
        alphaSeek = findViewById<View>(R.id.alphaControl) as SeekBar
        redSeek = findViewById<View>(R.id.redControl) as SeekBar
        greenSeek = findViewById<View>(R.id.greenControl) as SeekBar
        blueSeek = findViewById<View>(R.id.blueControl) as SeekBar

        alphaSeek.setOnSeekBarChangeListener(this)
        redSeek.setOnSeekBarChangeListener(this)
        greenSeek.setOnSeekBarChangeListener(this)
        blueSeek.setOnSeekBarChangeListener(this)

        alpha = shared.alpha
        red = shared.red
        green = shared.green
        blue = shared.blue

        alphaSeek.progress = alpha
        redSeek.progress = red
        greenSeek.progress = green
        blueSeek.progress = blue

        updateColor()
    }

    private fun stopServiceIfActive() {
        if (FilterService.STATE === FilterService.ACTIVE) {
            val i = Intent(this@MainActivity, FilterService::class.java)
            stopService(i)
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                   fromUser: Boolean) {
        if (seekBar === alphaSeek) {
            alpha = seekBar.progress
        }
        if (seekBar === redSeek) {
            red = seekBar.progress
        }
        if (seekBar === greenSeek) {
            green = seekBar.progress
        }
        if (seekBar === blueSeek) {
            blue = seekBar.progress
        }
        updateColor()
    }

    private fun updateColor() {
        val color = com.blackfilter.SharedMemory.getColor(alpha, red, green, blue)
        val cd = ColorDrawable(color)
        window.setBackgroundDrawable(cd)
    }

    override fun onStartTrackingTouch(sb: SeekBar) {}

    override fun onStopTrackingTouch(sb: SeekBar) {}

    fun cancelClick(v: View) {
        finish()
    }

    fun applyClick(v: View) {
        shared.alpha = alpha
        shared.red = red
        shared.green = green
        shared.blue = blue

        val i = Intent(this@MainActivity, FilterService::class.java)
        startService(i)

        makeNotification()
        finish()
    }

    fun makeNotification() {
        var nb = NotificationCompat.Builder(this)
        nb.setSmallIcon(R.drawable.notification_template_icon_bg)
        nb.setContentTitle("Shady Screen Filter")
        nb.setContentText("Active")
        nb.setAutoCancel(true)
        val resultIntent = Intent(this, MainActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(resultIntent)
        var resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        nb.setContentIntent(resultPendingIntent)
        var nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(0x355, nb.build())
    }
}