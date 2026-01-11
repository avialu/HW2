package com.avialu.hw2

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    private lateinit var grid: GridLayout
    private lateinit var btnMenu: Button

    private lateinit var txtScore: TextView
    private lateinit var txtDistance: TextView
    private lateinit var txtLives: TextView
    private lateinit var btnLeft: Button
    private lateinit var btnRight: Button
    private lateinit var buttonsControls: View

    private lateinit var vib: VibrationService
    private lateinit var sounds: SoundService

    private val handler = Handler(Looper.getMainLooper())
    private var running = false

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (!running) return
            val now = System.currentTimeMillis()
            if (now - lastTiltMoveMs < 180) return

            val x = event.values[0]
            if (x > tiltThreshold) {
                if (playerCol > 0) playerCol--
                lastTiltMoveMs = now
            } else if (x < -tiltThreshold) {
                if (playerCol < cols - 1) playerCol++
                lastTiltMoveMs = now
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private var lastTiltMoveMs = 0L
    private val tiltThreshold = 2.2f

    private val rows = 10
    private val cols = 5

    private var playerCol = 2

    data class Obstacle(var row: Int, var col: Int)
    data class Coin(var row: Int, var col: Int)

    private val obstacles = ArrayList<Obstacle>()
    private val coins = ArrayList<Coin>()

    private var spawnObsCounter = 0
    private var spawnCoinCounter = 0

    private var spawnObsEveryTicks = 2
    private var spawnCoinEveryTicks = 3
    private var tickMs = 250L

    private var score = 0
    private var distance = 0
    private var lives = 3

    private var controlMode: ControlMode = ControlMode.BUTTONS
    private var speedMode: SpeedMode = SpeedMode.SLOW

    private lateinit var cells: Array<Array<TextView>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        vib = VibrationService(this)
        sounds = SoundService()

        controlMode = intent.getStringExtra(EXTRA_CONTROL_MODE)
            ?.let { ControlMode.valueOf(it) } ?: ControlMode.BUTTONS

        speedMode = intent.getStringExtra(EXTRA_SPEED_MODE)
            ?.let { SpeedMode.valueOf(it) } ?: SpeedMode.SLOW

        bindViews()
        initCellsArray()
        setupGrid()
        setupControls()
        startGame()
    }

    private fun bindViews() {
        grid = findViewById(R.id.game_grid)
        txtScore = findViewById(R.id.txt_score)
        txtDistance = findViewById(R.id.txt_distance)
        txtLives = findViewById(R.id.txt_lives)
        btnLeft = findViewById(R.id.btn_left)
        btnRight = findViewById(R.id.btn_right)
        buttonsControls = findViewById(R.id.buttons_controls)
        btnMenu = findViewById(R.id.btn_menu)
    }

    private fun initCellsArray() {
        cells = Array(rows) { Array(cols) { TextView(this) } }
    }

    private fun setupGrid() {
        grid.removeAllViews()
        grid.rowCount = rows
        grid.columnCount = cols

        grid.post {
            grid.removeAllViews()

            val cellW = grid.width / cols
            val cellH = grid.height / rows

            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val tv = TextView(this)
                    val lp = GridLayout.LayoutParams(
                        GridLayout.spec(r, 1f),
                        GridLayout.spec(c, 1f)
                    ).apply {
                        width = cellW
                        height = cellH
                        setGravity(Gravity.CENTER)
                        setMargins(0, 0, 0, 0)
                    }
                    tv.layoutParams = lp
                    tv.gravity = Gravity.CENTER
                    tv.textSize =
                        (minOf(cellW, cellH) * 0.55f) / resources.displayMetrics.scaledDensity
                    tv.text = ""
                    tv.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    grid.addView(tv)
                    cells[r][c] = tv
                }
            }

            render()
        }
    }

    private fun setupControls() {
        buttonsControls.visibility =
            if (controlMode == ControlMode.BUTTONS) View.VISIBLE else View.GONE

        btnLeft.setOnClickListener {
            if (playerCol > 0) playerCol--
        }
        btnRight.setOnClickListener {
            if (playerCol < cols - 1) playerCol++
        }

        if (controlMode == ControlMode.SENSORS) {
            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }

        btnMenu.setOnClickListener {
            running = false
            handler.removeCallbacksAndMessages(null)
            val i = Intent(this, MenuActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (controlMode == ControlMode.SENSORS) {
            val s = accelerometer
            val sm = sensorManager
            if (s != null && sm != null) {
                sm.registerListener(sensorListener, s, SensorManager.SENSOR_DELAY_GAME)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (controlMode == ControlMode.SENSORS) {
            sensorManager?.unregisterListener(sensorListener)
        }
    }

    private fun applySpeed() {
        if (speedMode == SpeedMode.FAST) {
            tickMs = 180L
            spawnObsEveryTicks = 2
            spawnCoinEveryTicks = 3
        } else {
            tickMs = 250L
            spawnObsEveryTicks = 3
            spawnCoinEveryTicks = 4
        }
    }


    private fun startGame() {
        applySpeed()

        running = true
        obstacles.clear()
        coins.clear()
        spawnObsCounter = 0
        spawnCoinCounter = 0
        score = 0
        distance = 0
        lives = 3
        playerCol = 2

        spawnObstaclesTop()
        spawnCoinTop()

        render()
        handler.postDelayed(gameLoop, tickMs)
    }

    private val gameLoop = object : Runnable {
        override fun run() {
            if (!running) return

            tick()
            render()

            if (lives <= 0) {
                endGame()
                return
            }

            handler.postDelayed(this, tickMs)
        }
    }

    private fun spawnObstaclesTop() {
        val count = Random.nextInt(1, 4)
        val set = HashSet<Int>()
        while (set.size < count) set.add(Random.nextInt(cols))
        for (c in set) obstacles.add(Obstacle(0, c))
    }

    private fun spawnCoinTop() {
        val count = Random.nextInt(1, 3)
        val set = HashSet<Int>()
        while (set.size < count) set.add(Random.nextInt(cols))
        for (c in set) coins.add(Coin(0, c))
    }

    private fun tick() {
        distance += 1
        score += 1

        for (o in obstacles) o.row += 1
        for (co in coins) co.row += 1

        obstacles.removeAll { it.row >= rows }
        coins.removeAll { it.row >= rows }

        spawnObsCounter += 1
        if (spawnObsCounter >= spawnObsEveryTicks) {
            spawnObsCounter = 0
            spawnObstaclesTop()
        }

        spawnCoinCounter += 1
        if (spawnCoinCounter >= spawnCoinEveryTicks) {
            spawnCoinCounter = 0
            spawnCoinTop()
        }

        val playerRow = rows - 1

        val collectedCoins = coins.filter { it.row == playerRow && it.col == playerCol }
        if (collectedCoins.isNotEmpty()) {
            score += 5 * collectedCoins.size
            vib.coin()
            sounds.coin()
            coins.removeAll(collectedCoins.toSet())
        }

        val hitObstacles = obstacles.filter { it.row == playerRow && it.col == playerCol }
        if (hitObstacles.isNotEmpty()) {
            lives -= hitObstacles.size
            vib.crash()
            sounds.crash()
            obstacles.removeAll(hitObstacles.toSet())
        }

        if (lives < 0) lives = 0
    }

    private fun render() {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                cells[r][c].text = ""
            }
        }

        for (o in obstacles) {
            if (o.row in 0 until rows) {
                cells[o.row][o.col].text = "🏢"
            }
        }

        for (co in coins) {
            if (co.row in 0 until rows) {
                if (cells[co.row][co.col].text.isEmpty()) {
                    cells[co.row][co.col].text = "🪙"
                }
            }
        }

        val playerRow = rows - 1
        cells[playerRow][playerCol].text = "🏎️"

        txtScore.text = "Score: $score"
        txtDistance.text = "Dist: $distance"
        txtLives.text = "Lives: " + "❤".repeat(lives)
    }

    private fun endGame() {
        running = false
        handler.removeCallbacksAndMessages(null)

        val lat = 32.0853 + Random.nextDouble(-0.01, 0.01)
        val lng = 34.7818 + Random.nextDouble(-0.01, 0.01)

        ScoresRepository(this).add(
            ScoreRecord(
                score = score,
                distance = distance,
                timestamp = System.currentTimeMillis(),
                lat = lat,
                lng = lng
            )
        )

        startActivity(Intent(this, RecordsActivity::class.java))
        finish()
    }

    override fun onStop() {
        super.onStop()
        running = false
        handler.removeCallbacksAndMessages(null)
        if (controlMode == ControlMode.SENSORS) {
            sensorManager?.unregisterListener(sensorListener)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::sounds.isInitialized) sounds.release()
    }

    companion object {
        const val EXTRA_CONTROL_MODE = "CONTROL_MODE"
        const val EXTRA_SPEED_MODE = "SPEED_MODE"
    }
}
