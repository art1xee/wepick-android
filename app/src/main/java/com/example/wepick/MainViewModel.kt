package com.example.wepick

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.Exception

enum class ContentType { MOVIE, TV, ANIME }


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
        loadContent(ContentType.MOVIE, apiKey = BuildConfig.TMDB_API_KEY)
        loadContent(ContentType.TV, BuildConfig.TMDB_API_KEY)
        loadContent(ContentType.ANIME, "")
    }

    private val _items = mutableStateOf<List<ContentItem>>(emptyList())
    val items: State<List<ContentItem>> = _items

    private val _userName = mutableStateOf("")
    val userName: State<String> = _userName

    fun setUserName(name: String) {
        _userName.value = name
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

    fun loadContent(type: ContentType, apiKey: String) {
        viewModelScope.launch {
            try {
                val contentList: List<ContentItem> = when (type) {
                    ContentType.MOVIE -> RetrofitClient.instanceTmdb.getPopularMovies(apiKey).results
                    ContentType.TV -> RetrofitClient.instanceTmdb.getPopularTV(apiKey).results
                    ContentType.ANIME -> RetrofitClient.instanceJikan.getTopAnime().data.map { it.toContentItem() }
                }
                _items.value = contentList

                logContent(contentList, type)
            } catch (e: Exception) {
                println("CAUGHT ERROR: ${e.message}")
            }
        }
    }

}



