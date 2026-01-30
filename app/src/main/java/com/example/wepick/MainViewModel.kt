package com.example.wepick

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.Locale

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

    private val _friendName = mutableStateOf("")

    val friendName: State<String> = _friendName

    fun setFriendName(friendName: String) {
        _friendName.value = friendName
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

    fun setPartnerType(type: String) {
        _partnerType.value = type
        _partnerName.value = ""
    }

    // for choose genres in GenresScreen.kt
    var currentStep by mutableStateOf("dislikes")

    private val _selectedDislikes = mutableStateListOf<String>()
    val selectedDislikes: List<String> = _selectedDislikes

    private val _selectedLikes = mutableStateListOf<String>()
    val selectedLikes: List<String> = _selectedLikes

    private val _selectedDislikesFriend = mutableStateListOf<String>()
    val selectedDislikesFriend: List<String> = _selectedDislikesFriend

    private val _selectedLikesFriend = mutableStateListOf<String>()
    val selectedLikesFriend: List<String> = _selectedLikesFriend


    val decades = listOf(1920, 1930, 1940, 1950, 1960, 1970, 1980, 1990, 2000, 2010, 2020)
    fun nextDecade() {
        val currentDecadeValue = if (activePlayer == 1) selectedDecade else selectedDecadeFriend

        val currentIndex = decades.indexOf(currentDecadeValue)

        val nextIndex = (currentIndex + 1) % decades.size
        val newValue = decades[nextIndex]

        if (activePlayer == 1) {
            selectedDecade = newValue
        } else {
            selectedDecadeFriend = newValue
        }
    }

    fun prevDecade() {
        val currentDecadeValue = if (activePlayer == 1) selectedDecade else selectedDecadeFriend

        val currentIndex = decades.indexOf(currentDecadeValue)

        val prevIndex = if (currentIndex <= 0) decades.size - 1 else currentIndex - 1
        val newValue = decades[prevIndex]

        if (activePlayer == 1) {
            selectedDecade = newValue
        } else {
            selectedDecadeFriend = newValue
        }
    }

    fun toggleGenre(genre: String, isDislike: Boolean) {
        val list = if (activePlayer == 1) {
            if (isDislike) _selectedDislikes else _selectedLikes
        } else {
            if (isDislike) _selectedDislikesFriend else _selectedLikesFriend
        }

        if (list.contains(genre)) {
            list.remove(genre)
        } else if (list.size < 3) {
            list.add(genre)
        }
    }

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun showLockedError(message: String) {
        errorMessage = message
        viewModelScope.launch {
            delay(2000)
            errorMessage = null
        }
    }

    var isPartnerFriend by mutableStateOf(false)

    var activePlayer by mutableIntStateOf(1)

    fun prepareForGenres() {
        activePlayer = 1
        currentStep = "dislikes"

        _selectedLikes.clear()
        _selectedDislikes.clear()
    }

    var selectedDecade by mutableIntStateOf(2000)
    var selectedDecadeFriend by mutableIntStateOf(2000)

    fun setPartnerIsFriend(isFriend: Boolean) {
        isPartnerFriend = isFriend
    }

    var selectedCharacterName by mutableStateOf("")

    fun selectCharacter(name: String) {
        selectedCharacterName = name
    }


    fun generateCharacterFullProfile() {
        val allGenres = GenresData.GENRES["ru"] ?: emptyList()
        val shuffled = allGenres.shuffled()

        _selectedLikesFriend.clear()
        _selectedLikesFriend.addAll(shuffled.take(3))

        _selectedDislikesFriend.clear()
        _selectedDislikesFriend.addAll(shuffled.drop(3).take(3))

        selectedDecadeFriend = decades.random()
    }

    fun getGenresId(selectedGenre: String): Int? {
        val currentLang = "ru"
        val index = GenresData.GENRES[currentLang]?.indexOf(selectedGenre) ?: -1

        if (index != -1) {
            val englishGenreName = GenresData.GENRES["en"]?.get(index)
            return GenresData.tmdbGenreIds[englishGenreName]
        }
        println("DEBUG: Genre ID is not found for: $selectedGenre")
        return null
    }

    fun getGenreIdForApi(selectedGenres: String, type: ContentType): Int? {
        val currentLang = "ru"
        val index = GenresData.GENRES[currentLang]?.indexOf(selectedGenres.trim()) ?: -1

        if (index == -1) return null

        val englishName = GenresData.GENRES["en"]?.get(index)

        return if (type == ContentType.Anime) {
            GenresData.jikanGenreIds[englishName]
        } else {
            GenresData.tmdbGenreIds[englishName]
        }
    }

    fun processMatches(navController: NavController) {
        viewModelScope.launch {
            try {
                val type = selectedContentType.value ?: ContentType.Movie

                val allLikes = (selectedLikes + selectedLikesFriend).distinct()
                val allDislikes = (selectedDislikes + selectedDislikesFriend).distinct()
                val matchingGenres = allLikes.filter { it !in allDislikes }

                val genreIdsString = matchingGenres.mapNotNull { getGenreIdForApi(it,type) }.joinToString(",")

                val minYear = minOf(selectedDecade, selectedDecadeFriend)
                val maxYear = maxOf(selectedDecade, selectedDecadeFriend) + 9

                when (type) {
                    ContentType.Anime -> {
                        performJikanSeacrh(genreIdsString, minYear, maxYear)
                    }

                    else -> {
                        performTmdbDiscover(type, genreIdsString, minYear, maxYear)
                    }
                }
                navController.navigate(ScreenNav.Match.route)
            } catch (e: kotlin.Exception) {
                println("Search Error: ${e.message}")
            }
        }
    }

    private suspend fun performJikanSeacrh(genreIds: String, startYear: Int, endYear: Int) {
        try {
            val response = RetrofitClient.instanceJikan.searchAnime(
                genres = genreIds.ifEmpty { null }
            )

            val filtered = response.data.map {
                it.toContentItem()
            }.filter { anime ->
                val year = anime.releaseDate.toIntOrNull() ?: 0
                year in startYear..endYear
            }
            _items.value = filtered
        } catch (e: kotlin.Exception) {
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
        } catch (e: kotlin.Exception) {
            println("TMDB Discover Error: ${e.message}")
            _items.value = emptyList()
        }
    }


    fun loadContent(type: ContentType, apiKey: String) {
        viewModelScope.launch {
            try {
                val contentList: List<ContentItem> = when (type) {
                    ContentType.Movie -> RetrofitClient.instanceTmdb.getPopularMovies(apiKey).results.map {
                        it.toContentItem()
                    }
                    ContentType.Tv -> RetrofitClient.instanceTmdb.getPopularTV(apiKey).results.map {
                        it.toContentItem()
                    }
                    ContentType.Anime -> RetrofitClient.instanceJikan.getTopAnime().data.map {
                        it.toContentItem()
                    }
                }
                _items.value = contentList

                logContent(contentList, type)
            } catch (e: Exception) {
                println("CAUGHT ERROR: ${e.message}")
            }
        }
    }

}
