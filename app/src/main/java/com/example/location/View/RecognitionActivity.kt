package com.example.location.View

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.location.R
import com.example.location.Util.ActivityRecognizer.BackgroundDetectedActivitiesService
import com.example.location.databinding.ActivityRecognitionBinding
import com.google.android.gms.location.DetectedActivity

class RecognitionActivity : AppCompatActivity() {
    lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var binding:ActivityRecognitionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecognitionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "activity_intent") {
                    val activity = intent.getStringExtra("activity")
                    val transition = intent.getStringExtra("transition")
                    binding.tvRecog.text = "$activity  $transition"
                }
            }
        }

        binding.button.setOnClickListener(){
            val intent = Intent(this, BackgroundDetectedActivitiesService::class.java)
            startService(intent)
        }
    }



    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
            IntentFilter("activity_intent")
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }
}