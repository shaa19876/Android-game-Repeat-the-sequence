package com.example.exercise2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.exercise2.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
  private lateinit var binding: FragmentHomeBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentHomeBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.buttonNewGame.setOnClickListener {
      findNavController().navigate(R.id.action_homeFragment_to_gameFragment)
    }

    binding.buttonFreePlay.setOnClickListener {
      findNavController().navigate(R.id.action_homeFragment_to_freeGameFragment)
    }
  }
}