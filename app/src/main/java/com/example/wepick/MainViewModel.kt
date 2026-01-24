package com.example.wepick

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.Exception

enum class ContentType { MOVIE, TV, ANIME }

class MainViewModel : ViewModel() {

    init {
        loadContent(ContentType.MOVIE, "f0d0bc12560c00cff720536f062f5463")
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

                contentList.forEach {
                    println("WEPICK_DEBUG: loaded item: ${it.title}")
                }
            } catch (e: Exception) {
                println("CAUGHT ERROR: ${e.message}")
            }
        }
    }

}
