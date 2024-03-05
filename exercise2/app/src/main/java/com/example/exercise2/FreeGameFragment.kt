package com.example.exercise2

import android.content.SharedPreferences
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.exercise2.databinding.FragmentFreeBinding
import android.media.MediaPlayer

class FreeGameFragment : Fragment(R.layout.fragment_free) {
  private lateinit var binding: FragmentFreeBinding
  private lateinit var soundMap: Map<Button, Int>
  private var recordScore = 0

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
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.buttonHome.setOnClickListener {
      findNavController().navigate(R.id.action_freeGameFragment_to_homeFragment)
    }

    soundMap = mapOf(
      binding.button1 to R.raw.caw,
      binding.button2 to R.raw.horse,
      binding.button3 to R.raw.chicken,
      binding.button4 to R.raw.frog
    )

    binding.button1.setOnClickListener { playButton(binding.button1) }
    binding.button2.setOnClickListener { playButton(binding.button2) }
    binding.button3.setOnClickListener { playButton(binding.button3) }
    binding.button4.setOnClickListener { playButton(binding.button4) }

  }

  private fun playButton(button: Button) {
    val sound = soundMap[button]?.let { MediaPlayer.create(context, it) }
    while (sound!!.currentPosition < 1000) sound.start()
    sound.release()
  }
}