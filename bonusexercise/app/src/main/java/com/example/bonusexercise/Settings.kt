package com.example.bonusexercise

import android.widget.Button

class Settings {
  var sound: Boolean = true
  var delaySound: Long = 500
  var btnHighlight: Boolean = true
  var soundTheme: SoundTheme = SoundTheme.ANIMAL
}

enum class SoundTheme(val value: Set<Int>) {
  ANIMAL(
    setOf<Int>(
      R.raw.caw,
      R.raw.horse,
      R.raw.chicken,
      R.raw.frog
    )
  ),
  MUSIC(
    setOf<Int>(
      R.raw.music1,
      R.raw.music2,
      R.raw.music3,
      R.raw.music4
    )
  ),
  NOTES(
    setOf<Int>(
      R.raw.nota_do,
      R.raw.nota_re,
      R.raw.nota_mi,
      R.raw.nota_fa
    )
  )
}