package com.example.wepick

import android.content.Context
import androidx.core.content.edit

object LocaleSettings {
    private const val PREFS_NAME = "wepick_settings"
    private const val KEY_LANG = "selected_lang"

    fun saveLanguage(context: Context, lang: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_LANG, lang) }
    }

    fun getLanguage(context: Context?): String {
        val prefs = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs?.getString(KEY_LANG, "en") ?: "en"
    }
}