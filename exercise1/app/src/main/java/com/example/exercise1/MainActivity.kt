package com.example.exercise1

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.example.exercise1.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {

  private lateinit var binding: ActivityMainBinding
  private lateinit var soundMap: Map<Button, Int>
  private var listOfSounds = mutableListOf<Button>()
  private var listOfButtons = mutableListOf<Button>()
  private var countRightAnswer = 0
  private var levelScore = 0
  private var recordScore = 0

  private var sharedPref: SharedPreferences? = null


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    sharedPref = getPreferences(Context.MODE_PRIVATE)
    recordScore = sharedPref!!.getInt(getString(R.string.record_score), 0)
    binding.recordScore.text = recordScore.toString()
  }

  override fun onResume() {
    super.onResume()

    soundMap = mapOf(
      binding.button1 to R.raw.caw,
      binding.button2 to R.raw.horse,
      binding.button3 to R.raw.chicken,
      binding.button4 to R.raw.frog
    )

    binding.button1.setOnClickListener { playerInput(binding.button1) }
    binding.button2.setOnClickListener { playerInput(binding.button2) }
    binding.button3.setOnClickListener { playerInput(binding.button3) }
    binding.button4.setOnClickListener { playerInput(binding.button4) }

    binding.root.post {
      startGame()
    }

  }

  private fun playList() {
    disableButtons()
    CoroutineScope(Dispatchers.Default).launch {
      listOfSounds.forEach {
        withContext(Dispatchers.Main) { it.isPressed = true }
        val job = launch { playButton(it) }
        job.join()
        withContext(Dispatchers.Main) { it.isPressed = false }
        delay(500)
      }
      enableButtons()
    }
  }

  private fun playButton(button: Button) {
    val sound = soundMap[button]?.let { MediaPlayer.create(this, it) }
    while (sound!!.currentPosition < 1000) sound.start()
    sound.release()
  }

  private fun disableButtons() {
    binding.button1.isClickable = false
    binding.button2.isClickable = false
    binding.button3.isClickable = false
    binding.button4.isClickable = false
  }

  private fun enableButtons() {
    binding.button1.isClickable = true
    binding.button2.isClickable = true
    binding.button3.isClickable = true
    binding.button4.isClickable = true
  }

  private fun playerInput(btn: Button) {
    playButton(btn)

    if (btn == listOfSounds[countRightAnswer]) {
      listOfButtons.add(btn)
      countRightAnswer++
      if (listOfButtons == listOfSounds) {
        listOfSounds.add(soundMap.keys.toList().random())
        levelScore++
        binding.levelScore.text = levelScore.toString()
        listOfButtons.clear()
        countRightAnswer = 0
        playList()
      }
    } else {
      val builder = AlertDialog.Builder(this)
      builder.setTitle("Your score: $levelScore")
      builder.setPositiveButton("Restart") { _, _ ->
        recordScore = levelScore
        binding.recordScore.text = recordScore.toString()
        with(sharedPref!!.edit()) {
          putInt(getString(R.string.record_score), recordScore)
          apply()
        }

        levelScore = 0
        binding.levelScore.text = levelScore.toString()
        countRightAnswer = 0
        startGame()
      }
      val dialog = builder.create()
      dialog.show()
    }
  }

  private fun startGame() {
    listOfSounds.clear()
    listOfButtons.clear()
    repeat(4) { listOfSounds.add(soundMap.keys.toList().random()) }
    playList()
  }
}

