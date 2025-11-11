package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.widget.Button
import android.widget.TextView



class MainActivity : AppCompatActivity() {

    private lateinit var timerBinder: TimerService.TimerBinder
    var isConnected = false

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            timerBinder = service as TimerService.TimerBinder
            isConnected = true
            timerBinder.setHandler(timerHandler)

        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isConnected = false
        }
    }

    private val timerHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            findViewById<TextView>(R.id.textView).text = msg.what.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Intent(this, TimerService:: class.java).also { intent ->
           bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        }

        findViewById<Button>(R.id.startButton).setOnClickListener {

            if (!timerBinder.isRunning){
                findViewById<Button>(R.id.startButton).text = "Pause"
                timerBinder.start(10)

            } else if(timerBinder.paused){
                findViewById<Button>(R.id.startButton).text = "Pause"
                timerBinder.pause()

            } else if (!timerBinder.paused){
                findViewById<Button>(R.id.startButton).text = "UnPause"
                timerBinder.pause()
            }
        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            findViewById<Button>(R.id.startButton).text = "start"
            findViewById<TextView>(R.id.textView).text = "0"
            timerBinder.stop()
        }
    }

    override fun onDestroy() {
        unbindService(serviceConnection)
        isConnected = false
        super.onDestroy()
    }


}