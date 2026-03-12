package com.example.wepick.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wepick.data.local.GenresData
import com.example.wepick.util.GenreStep
import com.example.wepick.util.Language
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel : ViewModel() {

    // Names
    var userName by mutableStateOf("")
    var friendName by mutableStateOf("")

    // Partner
    var isPartnerFriend by mutableStateOf(false)
    var selectedCharacterName by mutableStateOf("")

    fun selectCharacter(name: String) {
        selectedCharacterName = name
    }

    fun updateFriendName(name: String) {
        friendName = name
    }

    fun updateUserName(name: String) {
        userName = name
    }

    // Active player and step
    var activePlayer by mutableIntStateOf(1)
    var currentStep by mutableStateOf(GenreStep.DISLIKES)

    // Decades
    val decades = listOf(1920, 1930, 1940, 1950, 1960, 1970, 1980, 1990, 2000, 2010, 2020)
    var selectedDecade by mutableIntStateOf(2000)
    var selectedDecadeFriend by mutableIntStateOf(2000)

    fun nextDecade() = changeDecade(1)
    fun prevDecade() = changeDecade(-1)

    private fun changeDecade(step: Int) {
        val current = if (activePlayer == 1) selectedDecade else selectedDecadeFriend
        val nextIndex = (decades.indexOf(current) + step + decades.size) % decades.size
        val newValue = decades[nextIndex]
        if (activePlayer == 1) selectedDecade = newValue else selectedDecadeFriend = newValue
    }

    // Genres
    private val _selectedDislikes = mutableStateListOf<Int>()
    val selectedDislikes: List<Int> = _selectedDislikes

    private val _selectedLikes = mutableStateListOf<Int>()
    val selectedLikes: List<Int> = _selectedLikes

    private val _selectedDislikesFriend = mutableStateListOf<Int>()
    val selectedDislikesFriend: List<Int> = _selectedDislikesFriend

    private val _selectedLikesFriend = mutableStateListOf<Int>()
    val selectedLikesFriend: List<Int> = _selectedLikesFriend


    fun toggleGenre(index: Int, isDislike: Boolean) {
        val list = if (activePlayer == 1) {
            if (isDislike) _selectedDislikes else _selectedLikes
        } else {
            if (isDislike) _selectedDislikesFriend else _selectedLikesFriend
        }
        if (list.contains(index)) list.remove(index)
        else if (list.size < 3) list.add(index)
    }

    fun prepareForGenres() {
        activePlayer = 1
        currentStep = GenreStep.DISLIKES
        _selectedDislikes.clear()
        _selectedLikes.clear()
    }

    // Error (заблокированное действие)
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun showLockedError(message: String) {
        viewModelScope.launch {
            errorMessage = message
            delay(2000)
            errorMessage = null
        }
    }

    // Character
    fun generateCharacterFullProfile() {
        val indices = GenresData.GENRES[Language.EN]!!.indices.toList().shuffled()
        _selectedLikesFriend.clear()
        _selectedLikesFriend.addAll(indices.take(3))
        _selectedDislikesFriend.clear()
        _selectedDislikesFriend.addAll(indices.drop(3).take(3))
        selectedDecadeFriend = decades.random()
    }

    // Reset
    fun reset() {
        userName = ""
        friendName = ""
        isPartnerFriend = false
        selectedCharacterName = ""
        activePlayer = 1
        currentStep = GenreStep.DISLIKES
        selectedDecade = 2000
        selectedDecadeFriend = 2000
        _selectedDislikes.clear()
        _selectedLikes.clear()
        _selectedDislikesFriend.clear()
        _selectedLikesFriend.clear()
    }
}







