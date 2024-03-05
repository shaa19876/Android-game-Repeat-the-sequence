package com.example.bonusexercise

import android.content.SharedPreferences
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bonusexercise.databinding.FragmentFreeBinding
import android.media.MediaPlayer

class FreeGameFragment : Fragment(R.layout.fragment_free) {
  private lateinit var binding: FragmentFreeBinding
  private lateinit var soundMap: Map<Button, Int>
  private var recordScore = 0

  private val settings = Settings()

  private lateinit var sharedPref: SharedPreferences
  private lateinit var context: Context

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentFreeBinding.inflate(layoutInflater)
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
      findNavController().navigate(R.id.action_freeGameFragment_to_homeFragment)
    }

    soundMap = mapOf(
      binding.button1 to settings.soundTheme.value.elementAt(0),
      binding.button2 to settings.soundTheme.value.elementAt(1),
      binding.button3 to settings.soundTheme.value.elementAt(2),
      binding.button4 to settings.soundTheme.value.elementAt(3)
    )

    binding.button1.setOnClickListener { playButton(binding.button1) }
    binding.button2.setOnClickListener { playButton(binding.button2) }
    binding.button3.setOnClickListener { playButton(binding.button3) }
    binding.button4.setOnClickListener { playButton(binding.button4) }

  }

  private fun playButton(button: Button) {
    if (settings.sound) {
      val sound = soundMap[button]?.let { MediaPlayer.create(context, it) }
      while (sound!!.currentPosition < 1000) sound.start()
      sound.release()
    }
  }

  private fun disableHighlightButtons() {
    binding.button1.setBackgroundResource(R.drawable.button1_default)
    binding.button2.setBackgroundResource(R.drawable.button2_default)
    binding.button3.setBackgroundResource(R.drawable.button3_default)
    binding.button4.setBackgroundResource(R.drawable.button4_default)
  }

  private fun getSettings(sharedPref: SharedPreferences) {
    settings.sound = sharedPref.getBoolean(getString(R.string.sound_setting), true)
    settings.delaySound = sharedPref.getLong(getString(R.string.delay_setting), 500)
    settings.btnHighlight = sharedPref.getBoolean(getString(R.string.highlight_setting), true)
    settings.soundTheme = sharedPref.getString(getString(R.string.sound_theme_setting), "ANIMAL")
      ?.let { SoundTheme.valueOf(it) }!!
  }
}