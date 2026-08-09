package com.example.wepick.viewmodel

sealed class AuthTransitionState {
    data class Loading(val message: String) : AuthTransitionState()
    data class Success(val message: String) : AuthTransitionState()
}