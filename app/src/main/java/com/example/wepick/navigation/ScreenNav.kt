package com.example.wepick.navigation

sealed class ScreenNav(val route: String) {
    object Login : ScreenNav("login") // Экран для входа/регестрации в аккаунт

    object ProfileSetup : ScreenNav("profile_setup")
    object Favorite : ScreenNav("favourite")
    object SignUp : ScreenNav("sign_up")
    object Help: ScreenNav("help")

    object ChangePassword: ScreenNav("change_password")

    object DeleteAccount: ScreenNav("delete_account")
    object PersonalData : ScreenNav("personal_data")
    object  AppSetting: ScreenNav("app_setting")

    object ForgotPassword : ScreenNav("forgot_password") // Screen when user forgot his password 

    object ProfileSettingScreen : ScreenNav("profile_setting_screen") // screen for settings
    object Home : ScreenNav("home")
    object Main : ScreenNav("main")             // Экран ввода имени

    object Selection : ScreenNav("selection")  // Выбор: Фильмы/ТВ/Аниме

    object Partner : ScreenNav("partner")      // Друг или Персонаж

    object FriendName : ScreenNav("friend_name")

    object CharacterPicker : ScreenNav("popular_character")

    object Genres : ScreenNav("genres")         // Выбор жанров

    object Summary : ScreenNav("summary")

    object Match : ScreenNav("match")            // Финальная карточка
}