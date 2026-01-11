package com.avialu.hw2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        findViewById<Button>(R.id.btn_buttons_slow).setOnClickListener {
            startGame(ControlMode.BUTTONS, SpeedMode.SLOW)
        }

        findViewById<Button>(R.id.btn_buttons_fast).setOnClickListener {
            startGame(ControlMode.BUTTONS, SpeedMode.FAST)
        }

        findViewById<Button>(R.id.btn_sensors).setOnClickListener {
            startGame(ControlMode.SENSORS, SpeedMode.SLOW)
        }

        findViewById<Button>(R.id.btn_records).setOnClickListener {
            startActivity(Intent(this, RecordsActivity::class.java))
        }
    }

    private fun startGame(mode: ControlMode, speed: SpeedMode) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra(GameActivity.EXTRA_CONTROL_MODE, mode.name)
        intent.putExtra(GameActivity.EXTRA_SPEED_MODE, speed.name)
        startActivity(intent)
    }
}
