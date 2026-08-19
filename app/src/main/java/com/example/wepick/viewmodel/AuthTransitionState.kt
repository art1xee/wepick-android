package com.example.wepick.viewmodel

import com.example.wepick.util.UiText

sealed class AuthTransitionState {
    data class Loading(val message: UiText.StringResource) : AuthTransitionState()
    data class Success(val message: UiText.StringResource) : AuthTransitionState()
}