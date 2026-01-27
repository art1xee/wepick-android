package com.example.wepick

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.Exception

sealed class ContentType(val name: String) {
    object Movie : ContentType("movie")
    object Tv : ContentType("series")
    object Anime : ContentType("anime")
}


class MainViewModel : ViewModel() {
    private fun logContent(items: List<ContentItem>, type: ContentType) {
        if (items.isEmpty()) {
            println("WEPICK_DEBUG: [$type] the list is empty")
            return
        }
        println("WEPICK_DEBUG: The start of the list [$type]")
        items.forEach { item ->
            println("WEPICK_DEBUG: ID: ${item.id} | Title: ${item.title} | Rating: ${item.rating}")
        }
        println("WEPICK_DEBUG: The end of the list. ${items.size}")
    }

    init {
        loadContent(ContentType.Movie, apiKey = BuildConfig.TMDB_API_KEY)
        loadContent(ContentType.Tv, BuildConfig.TMDB_API_KEY)
        loadContent(ContentType.Anime, "")
    }

    private val _items = mutableStateOf<List<ContentItem>>(emptyList())
    val items: State<List<ContentItem>> = _items

    private val _userName = mutableStateOf("")
    val userName: State<String> = _userName

    fun setUserName(name: String) {
        _userName.value = name
    }

    private val _selectedContentType = mutableStateOf<ContentType?>(null)
    val selectedContentType: State<ContentType?> = _selectedContentType

    fun setContentType(type: ContentType) {
        _selectedContentType.value = type
    }

    /// who is a partner: Friend or Character?
    private val _partnerType = mutableStateOf("")
    val partnerType: State<String> = _partnerType

    // friend name or ID character
    private val _partnerName = mutableStateOf("")
    val partnerName: State<String> = _partnerName

    fun setPartner(type: String, name: String) {
        _partnerType.value = type
        _partnerName.value = name
    }

    // for choose genres in GenresScreen.kt
    var currentStep by mutableStateOf("dislikes")

    private val _selectedDislikes = mutableStateListOf<String>()
    val selectedDislikes: List<String> = _selectedDislikes

    private val _selectedLikes = mutableStateListOf<String>()
    val selectedLikes: List<String> = _selectedLikes

    var selectedDecade by mutableIntStateOf(2000)

    fun toggleDislike(genre: String) {
        if (_selectedDislikes.contains(genre)) _selectedDislikes.remove(genre)
        else if (_selectedDislikes.size < 3) _selectedDislikes.add(genre)
    }

    fun toggleLikes(genre: String) {
        if (_selectedDislikes.contains(genre)) return
        if (_selectedLikes.contains(genre)) _selectedLikes.remove(genre)
        else if (_selectedLikes.size < 3) _selectedLikes.add(genre)
    }

    fun loadContent(type: ContentType, apiKey: String) {
        viewModelScope.launch {
            try {
                val contentList: List<ContentItem> = when (type) {
                    ContentType.Movie -> RetrofitClient.instanceTmdb.getPopularMovies(apiKey).results
                    ContentType.Tv -> RetrofitClient.instanceTmdb.getPopularTV(apiKey).results
                    ContentType.Anime -> RetrofitClient.instanceJikan.getTopAnime().data.map { it.toContentItem() }
                }
                _items.value = contentList

                logContent(contentList, type)
            } catch (e: Exception) {
                println("CAUGHT ERROR: ${e.message}")
            }
        }
    }

}



