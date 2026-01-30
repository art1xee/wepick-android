package com.example.wepick

import android.content.Context
import java.util.Locale

object LocalHelper {
    fun updateResources(context: Context?, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = context?.resources
        val configuration = resources?.configuration
        configuration?.setLocale(locale)
        configuration?.setLayoutDirection(locale)

        resources?.updateConfiguration(configuration, resources.displayMetrics)
    }
}
