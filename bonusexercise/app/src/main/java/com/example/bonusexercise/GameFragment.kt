package com.example.bonusexercise

import android.app.AlertDialog
import android.content.SharedPreferences
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bonusexercise.databinding.FragmentGameBinding
import kotlinx.coroutines.*
import android.media.MediaPlayer

class GameFragment : Fragment(R.layout.fragment_game) {
  private lateinit var binding: FragmentGameBinding
  private lateinit var soundMap: Map<Button, Int>
  private lateinit var scope: CoroutineScope
  private var listOfSounds = mutableListOf<Button>()
  private var listOfButtons = mutableListOf<Button>()
  private var countRightAnswer = 0
  private var levelScore = 0
  private var recordScore = 0

  private val settings = Settings()

  private lateinit var sharedPref: SharedPreferences
  private lateinit var context: Context

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentGameBinding.inflate(layoutInflater)
    context = requireContext()
    sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
    recordScore = sharedPref.getInt(getString(R.string.record_score), 0)
    binding.recordScore.text = recordScore.toString()

    getSettings(sharedPref)
    if (!settings.btnHighlight) disableHighlightButtons()

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.buttonHome.setOnClickListener {
      scope.cancel()
      findNavController().navigate(R.id.action_gameFragment_to_homeFragment)
    }

    soundMap = mapOf(
      binding.button1 to settings.soundTheme.value.elementAt(0),
      binding.button2 to settings.soundTheme.value.elementAt(1),
      binding.button3 to settings.soundTheme.value.elementAt(2),
      binding.button4 to settings.soundTheme.value.elementAt(3)
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
    scope = CoroutineScope(Dispatchers.Default)
    scope.launch {
      listOfSounds.forEach {
        if (settings.btnHighlight) withContext(Dispatchers.Main) { it.isPressed = true }
        val job = launch {
          playButton(it)
          if (!settings.sound) delay(settings.delaySound)
        }
        job.join()
        withContext(Dispatchers.Main) { it.isPressed = false }

        delay(settings.delaySound)
      }
      enableButtons()
    }
  }

  private fun playButton(button: Button) {
    if (settings.sound) {
      val sound = soundMap[button]?.let { MediaPlayer.create(context, it) }
      while (sound!!.currentPosition < 1000) sound.start()
      sound.release()
    }
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

  private fun disableHighlightButtons() {
    binding.button1.setBackgroundResource(R.drawable.button1_default)
    binding.button2.setBackgroundResource(R.drawable.button2_default)
    binding.button3.setBackgroundResource(R.drawable.button3_default)
    binding.button4.setBackgroundResource(R.drawable.button4_default)
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
      val builder = AlertDialog.Builder(context)
      builder.setTitle("Your score: $levelScore")
      builder.setPositiveButton("Restart") { _, _ ->
        recordScore = levelScore
        binding.recordScore.text = recordScore.toString()
        with(sharedPref.edit()) {
          putInt(getString(R.string.record_score), recordScore)
          apply()
        }

        levelScore = 0
        binding.levelScore.text = levelScore.toString()
        countRightAnswer = 0
        startGame()
      }
      builder.setNegativeButton("Menu") { _, _ ->
        binding.buttonHome.performClick()
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

  private fun getSettings(sharedPref: SharedPreferences) {
    settings.sound = sharedPref.getBoolean(getString(R.string.sound_setting), true)
    settings.delaySound = sharedPref.getLong(getString(R.string.delay_setting), 500)
    settings.btnHighlight = sharedPref.getBoolean(getString(R.string.highlight_setting), true)
    settings.soundTheme = sharedPref.getString(getString(R.string.sound_theme_setting), "ANIMAL")
      ?.let { SoundTheme.valueOf(it) }!!
  }
}