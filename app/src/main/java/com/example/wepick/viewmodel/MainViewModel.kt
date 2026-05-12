package com.example.wepick.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.wepick.MainActivity
import com.example.wepick.data.model.ContentType
import com.example.wepick.navigation.ScreenNav
import com.example.wepick.util.Language
import com.example.wepick.util.LocalHelper
import com.example.wepick.util.LocaleSettings

class MainViewModel : ViewModel() {

    // Content Type
    private val _selectedContentType = mutableStateOf<ContentType?>(null)
    val selectedContentType: State<ContentType?> = _selectedContentType
    fun setContentType(type: ContentType) {
        _selectedContentType.value = type
    }

    // Partner Type (Friend/Character)
    var partnerType by mutableStateOf("")
        private set

    fun updatePartnerType(type: String) {
        partnerType = type
    }

    // Menu
    private val _isMenuOpen = mutableStateOf(false)
    val isMenuOpen: State<Boolean> = _isMenuOpen

    fun toggleMenu() {
        _isMenuOpen.value = !_isMenuOpen.value
    }

    fun closeMenu() {
        _isMenuOpen.value = false
    }

    // Language
    private val _currentLanguage = mutableStateOf(Language.UK)
    val currentLanguage: State<String> = _currentLanguage

    fun initLanguage(context: Context) {
        _currentLanguage.value = LocaleSettings.getLanguage(context)
    }

    fun setLanguage(lang: String, context: Context) {
        LocaleSettings.saveLanguage(context, lang)
        _currentLanguage.value = lang
        LocalHelper.updateResources(context, lang)

        if (context is Activity) {
            context.recreate()
        } else {
            val intent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            context.startActivity(intent)
        }
    }

    // Navigation to results
    fun navigateToMatch(navController: NavController) {
        navController.navigate(ScreenNav.Match.route)
    }

    // All reset (recall reset on the another VM)
    fun resetAll(
        playerViewModel: PlayerViewModel,
        contentViewModel: ContentViewModel
    ) {
        _selectedContentType.value = null
        partnerType = ""
        _isMenuOpen.value = false
        playerViewModel.reset()
        contentViewModel.resetItems()
    }

}