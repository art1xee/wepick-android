package com.example.wepick.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wepick.data.local.GenresData
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
    var currentStep by mutableStateOf("dislikes")

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
    private val _selectedDislikes = mutableStateListOf<String>()
    val selectedDislikes: List<String> = _selectedDislikes

    private val _selectedLikes = mutableStateListOf<String>()
    val selectedLikes: List<String> = _selectedLikes

    private val _selectedDislikesFriend = mutableStateListOf<String>()
    val selectedDislikesFriend: List<String> = _selectedDislikesFriend

    private val _selectedLikesFriend = mutableStateListOf<String>()
    val selectedLikesFriend: List<String> = _selectedLikesFriend


    fun toggleGenre(genre: String, isDislike: Boolean) {
        val list = if (activePlayer == 1) {
            if (isDislike) _selectedDislikes else _selectedLikes
        } else {
            if (isDislike) _selectedDislikesFriend else _selectedLikesFriend
        }
        if (list.contains(genre)) list.remove(genre)
        else if (list.size < 3) list.add(genre)
    }

    fun prepareForGenres() {
        activePlayer = 1
        currentStep = "dislike"
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
        val allGenres = GenresData.GENRES["ru"] ?: emptyList()
        val shuffled = allGenres.shuffled()
        _selectedLikesFriend.clear()
        _selectedLikesFriend.addAll(shuffled.take(3))
        _selectedDislikesFriend.clear()
        _selectedDislikesFriend.addAll(shuffled.drop(3).take(3))
        selectedDecadeFriend = decades.random()
    }

    // Reset
    fun reset() {
        userName = ""
        friendName = ""
        isPartnerFriend = false
        selectedCharacterName = ""
        activePlayer = 1
        currentStep = "dislike"
        selectedDecade = 2000
        selectedDecadeFriend = 2000
        _selectedDislikes.clear()
        _selectedLikes.clear()
        _selectedDislikesFriend.clear()
        _selectedLikesFriend.clear()
    }
}







