package com.example.wepick

enum class ScreenNav(val route: String) {
    Main("main"),             // Экран ввода имени
    Selection("selection"),   // Выбор: Фильмы/ТВ/Аниме
    Partner("partner"),       // Друг или Персонаж
    Genres("genres"),         // Выбор жанров
    Match("match")            // Финальная карточка
}