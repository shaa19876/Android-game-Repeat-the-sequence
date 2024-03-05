package com.example.bonusexercise

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bonusexercise.databinding.FragmentAboutBinding

class AboutFragment : Fragment(R.layout.fragment_about) {
  private lateinit var binding: FragmentAboutBinding
  private var recordScore = 0

  private lateinit var sharedPref: SharedPreferences
  private lateinit var context: Context

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentAboutBinding.inflate(layoutInflater)
    context = requireContext()
    sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
    recordScore = sharedPref.getInt(getString(R.string.record_score), 0)
    binding.recordScore.text = recordScore.toString()
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.buttonHome.setOnClickListener {
      findNavController().navigate(R.id.action_aboutFragment_to_homeFragment)
    }
  }
}