package com.example.wepick.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wepick.BuildConfig
import com.example.wepick.data.local.GenresData
import com.example.wepick.data.model.ContentItem
import com.example.wepick.data.model.ContentType
import com.example.wepick.data.model.jikan.toContentItem
import com.example.wepick.data.model.tmdb.toContentItem
import com.example.wepick.data.network.RetrofitClient
import kotlinx.coroutines.launch


class ContentViewModel : ViewModel() {
    private val _items = mutableStateOf<List<ContentItem>>(emptyList())
    val items: State<List<ContentItem>> = _items

    private var randomPage = (1..20).random()

    init {
        loadContent(ContentType.Movie, BuildConfig.TMDB_API_KEY)
        loadContent(ContentType.Tv, BuildConfig.TMDB_API_KEY)
        loadContent(ContentType.Anime, BuildConfig.JIKAN_BASE_URL)
    }

    fun loadContent(type: ContentType, apiKey: String) {
        viewModelScope.launch {
            try {
                val contentList: List<ContentItem> = when (type) {
                    ContentType.Movie -> RetrofitClient.instanceTmdb.getPopularMovies(apiKey).results.map { it.toContentItem() }
                    ContentType.Tv -> RetrofitClient.instanceTmdb.getPopularTV(apiKey).results.map { it.toContentItem() }
                    ContentType.Anime -> RetrofitClient.instanceJikan.getTopAnime().data.map { it.toContentItem() }
                }
                _items.value = contentList
            } catch (e: Exception) {
                println("CAUGHT ERROR: ${e.message} ")
            }
        }
    }

    fun processMatches(
        type: ContentType,
        selectedLikes: List<String>,
        selectedLikesFriend: List<String>,
        selectedDislikes: List<String>,
        selectedDislikesFriend: List<String>,
        selectedDecade: Int,
        selectedDecadeFriend: Int,
        currentLanguage: String,
        onDone: () -> Unit,
    ) {
        randomPage = (1..20).random()
        viewModelScope.launch {
            try {
                val allLikes = (selectedLikes + selectedLikesFriend).distinct()
                val allDislikes = (selectedDislikes + selectedDislikesFriend).distinct()
                val matchingGenres = allLikes.filter { it !in allDislikes }

                val genreIdsString = matchingGenres
                    .mapNotNull { getGenreIdForApi(it, type, currentLanguage) }
                    .joinToString(",")

                val minYear = minOf(selectedDecade, selectedDecadeFriend)
                val maxYear = maxOf(selectedDecade, selectedDecadeFriend) + 9

                when (type) {
                    ContentType.Anime -> performJikanSearch(genreIdsString, minYear, maxYear)
                    else -> performTmdbDiscover(type, genreIdsString, minYear, maxYear)
                }
                onDone()
            } catch (e: Exception) {
                println("Search Error: ${e.message}")
            }
        }
    }

    private suspend fun performJikanSearch(genreIds: String, startYear: Int, endYear: Int) {
        try {
            val response = RetrofitClient.instanceJikan.searchAnime(
                genres = genreIds.ifEmpty { null },
                page = randomPage,
            )
            _items.value = response.data.map { it.toContentItem() }.filter { anime ->
                val year = anime.releaseDate.toIntOrNull() ?: 0
                year in startYear..endYear
            }
        } catch (e: Exception) {
            println("Jikan Error: ${e.message}")
            _items.value = emptyList()
        }
    }

    private suspend fun performTmdbDiscover(
        type: ContentType,
        genres: String,
        start: Int,
        end: Int
    ) {
        try {
            val response = if (type == ContentType.Movie) {
                RetrofitClient.instanceTmdb.getDiscoveryMovie(
                    apiKey = BuildConfig.TMDB_API_KEY,
                    genres = genres,
                    dateStart = "$start-01-01",
                    dateEnd = "$end-12-31",
                    page = randomPage,
                )
            } else {
                RetrofitClient.instanceTmdb.getDiscoveryTV(
                    apiKey = BuildConfig.TMDB_API_KEY,
                    genres = genres,
                    dateStart = "$start-01-01",
                    dateEnd = "$end-12-31",
                )
            }
            _items.value = response.results.map { it.toContentItem() }
        } catch (e: Exception) {
            println("TMDB Discover Error: ${e.message}")
            _items.value = emptyList()
        }
    }

    private fun getGenreIdForApi(selectedGenre: String, type: ContentType, lang: String): Int? {
        val index = GenresData.GENRES[lang]?.indexOf(selectedGenre.trim()) ?: -1
        if (index == -1) return null
        val englishName = GenresData.GENRES["en"]?.get(index)
        return if (type == ContentType.Anime) {
            GenresData.jikanGenreIds[englishName]
        } else {
            GenresData.tmdbGenreIds[englishName]
        }
    }

    fun resetItems() {
        _items.value = emptyList()
        loadContent(ContentType.Movie, BuildConfig.TMDB_API_KEY)
        loadContent(ContentType.Tv, BuildConfig.TMDB_API_KEY)
        loadContent(ContentType.Anime, BuildConfig.JIKAN_BASE_URL)
    }
}