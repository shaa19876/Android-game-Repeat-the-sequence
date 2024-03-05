package com.example.exercise2

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
import com.example.exercise2.databinding.FragmentGameBinding
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
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.buttonHome.setOnClickListener {
      scope.cancel()
      findNavController().navigate(R.id.action_gameFragment_to_homeFragment)
    }

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
    scope = CoroutineScope(Dispatchers.Default)
    scope.launch {
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
    val sound = soundMap[button]?.let { MediaPlayer.create(context, it) }
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