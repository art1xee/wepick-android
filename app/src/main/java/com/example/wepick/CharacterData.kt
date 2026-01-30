package com.example.wepick

data class Character(
    val id: String,
    val name: String,
    val imageRes: Int,
)

val characterList = listOf(
    Character("joker", "Joker", R.drawable.joker),
    Character("luffy", "Monkey D. Luffy", R.drawable.monkey_d_luffy),
    Character("chopper", "Chopper", R.drawable.chopper),
    Character("sherlock", "Sherlock H.", R.drawable.sherlock),
    Character("levi_ackerman", "Levi Ackerman", R.drawable.levi_ackerman),
    Character("walter_white", "Walter White", R.drawable.walter_white),
    Character("john_wick", "John Wick", R.drawable.john_wick),
    Character("saul_good_man", "Saul Good Man", R.drawable.saul_good_man),
    Character("arthur_morgan", "Arthur M.", R.drawable.arthur_morgan),
    )
