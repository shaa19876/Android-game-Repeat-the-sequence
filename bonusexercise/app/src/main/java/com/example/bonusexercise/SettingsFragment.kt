package com.example.bonusexercise

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bonusexercise.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {
  private lateinit var binding: FragmentSettingsBinding
  private val settings = Settings()

  private lateinit var sharedPref: SharedPreferences
  private lateinit var context: Context

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentSettingsBinding.inflate(layoutInflater)
    context = requireContext()
    sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
    binding.delayValue.text = binding.seekBar.progress.toString()
    getSettings(sharedPref)
    setSettings()
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.buttonHome.setOnClickListener {
      findNavController().navigate(R.id.action_settingsFragment_to_homeFragment)
    }

    //delay settings
    binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val step = 100
        val value = (progress / step) * step
        seekBar.progress = value
        binding.delayValue.text = value.toString()
      }

      override fun onStartTrackingTouch(seekBar: SeekBar) {}

      override fun onStopTrackingTouch(seekBar: SeekBar) {
        with(sharedPref.edit()) {
          putLong(getString(R.string.delay_setting), seekBar.progress.toLong())
          apply()
        }
      }
    })

    //sound on/off settings
    binding.switchSound.setOnCheckedChangeListener { _, isChecked ->
      with(sharedPref.edit()) {
        putBoolean(getString(R.string.sound_setting), isChecked)
        apply()
      }
    }

    //highlights button settings
    binding.switchHighlight.setOnCheckedChangeListener { _, isChecked ->
      with(sharedPref.edit()) {
        putBoolean(getString(R.string.highlight_setting), isChecked)
        apply()
      }
    }

    //sound theme settings
    binding.switchboxtheme.setOnCheckedChangeListener { _, checkedId ->
      val radioButton: RadioButton = binding.root.findViewById(checkedId)
      with(sharedPref.edit()) {
        putString(getString(R.string.sound_theme_setting), radioButton.text.toString())
        apply()
      }
    }
  }

  private fun getSettings(sharedPref: SharedPreferences) {
    settings.sound = sharedPref.getBoolean(getString(R.string.sound_setting), true)
    settings.delaySound = sharedPref.getLong(getString(R.string.delay_setting), 500)
    settings.btnHighlight = sharedPref.getBoolean(getString(R.string.highlight_setting), true)
    settings.soundTheme = sharedPref.getString(getString(R.string.sound_theme_setting), "ANIMAL")
      ?.let { SoundTheme.valueOf(it) }!!
  }

  private fun setSettings(){
    binding.delayValue.text = settings.delaySound.toString()
    binding.seekBar.progress = settings.delaySound.toInt()
    binding.switchSound.isChecked = settings.sound
    binding.switchHighlight.isChecked = settings.btnHighlight
    when (settings.soundTheme.name) {
      "ANIMAL" -> binding.radioButtonAnimal.isChecked = true
      "MUSIC" -> binding.radioButtonMusic.isChecked = true
      "NOTES" -> binding.radioButtonNotes.isChecked = true
    }
  }
}