package com.example.bonusexercise

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.bonusexercise.databinding.ActivityMainBinding

class MainActivity : FragmentActivity() {
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
  }
}
