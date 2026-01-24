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
        loadContent(ContentType.MOVIE, "f0d0bc12560c00cff720536f062f5463")
        loadContent(ContentType.TV, "f0d0bc12560c00cff720536f062f5463")
        loadContent(ContentType.ANIME, "")
    }

    private val _items = mutableStateOf<List<ContentItem>>(emptyList())
    val items: State<List<ContentItem>> = _items

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



