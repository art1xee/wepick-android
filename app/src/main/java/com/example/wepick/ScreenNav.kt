package com.example.wepick

sealed class ScreenNav(val route: String) {
    object Main : ScreenNav("main")             // Экран ввода имени

    object Selection : ScreenNav("selection")  // Выбор: Фильмы/ТВ/Аниме

    object Partner : ScreenNav("partner")      // Друг или Персонаж

    object FriendName : ScreenNav("friend_name")

    object CharacterPicker : ScreenNav("popular_character")

    object Genres : ScreenNav("genres")         // Выбор жанров

    object Summary: ScreenNav("summary")

    object Match : ScreenNav("match")            // Финальная карточка
}