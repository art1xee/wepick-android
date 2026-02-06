package com.example.wepick

import android.app.Activity
import android.content.Context
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

sealed class ContentType(val name: String) {
    object Movie : ContentType("movie")
    object Tv : ContentType("series")
    object Anime : ContentType("anime")
}

class MainViewModel : ViewModel() {

    private val _items = mutableStateOf<List<ContentItem>>(emptyList())
    val items: State<List<ContentItem>> = _items

    private val _userName = mutableStateOf("")
    val userName: State<String> = _userName

    private val _friendName = mutableStateOf("")
    val friendName: State<String> = _friendName

    private val _selectedContentType = mutableStateOf<ContentType?>(null)
    val selectedContentType: State<ContentType?> = _selectedContentType

    private val _partnerType = mutableStateOf("")
    val partnerType: State<String> = _partnerType

    private val _partnerName = mutableStateOf("")
    val partnerName: State<String> = _partnerName

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
    var selectedDecade by mutableIntStateOf(2000)
    var selectedDecadeFriend by mutableIntStateOf(2000)

    var isPartnerFriend by mutableStateOf(false)
    var activePlayer by mutableIntStateOf(1)
    var selectedCharacterName by mutableStateOf("")

    private val _isMenuOpen = mutableStateOf(false)
    val isMenuOpen: State<Boolean> = _isMenuOpen

    private val _currentLanguage = mutableStateOf("en")
    val currentLanguage: State<String> = _currentLanguage

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadContent(ContentType.Movie, apiKey = BuildConfig.TMDB_API_KEY)
        loadContent(ContentType.Tv, BuildConfig.TMDB_API_KEY)
        loadContent(ContentType.Anime, "")
    }

    fun initLanguage(context: Context) {
        _currentLanguage.value = LocaleSettings.getLanguage(context) ?: "en"
    }

    fun setLanguage(lang: String, context: Context) {
        LocaleSettings.saveLanguage(context, lang)
        _currentLanguage.value = lang
        LocalHelper.updateResources(context, lang)
        if (context is Activity) {
            context.recreate()
        }
    }

    fun resetGame() {
        _userName.value = ""
        _friendName.value = ""
        _selectedContentType.value = null
        _partnerType.value = ""
        _partnerName.value = ""
        currentStep = "dislikes"
        _selectedDislikes.clear()
        _selectedLikes.clear()
        _selectedDislikesFriend.clear()
        _selectedLikesFriend.clear()
        selectedDecade = 2000
        selectedDecadeFriend = 2000
        activePlayer = 1
        selectedCharacterName = ""
        isPartnerFriend = false
        _isMenuOpen.value = false
        _items.value = emptyList() // Очищаем результаты поиска
    }

    fun setUserName(name: String) { _userName.value = name }
    fun setFriendName(name: String) { _friendName.value = name }
    fun setContentType(type: ContentType) { _selectedContentType.value = type }
    
    fun setPartnerType(type: String) {
        _partnerType.value = type
        isPartnerFriend = (type == "friend")
        if (!isPartnerFriend) {
            _friendName.value = ""
        }
    }

    fun prepareForGenres() {
        activePlayer = 1
        currentStep = "dislikes"
        _selectedLikes.clear()
        _selectedDislikes.clear()
        if (isPartnerFriend) {
            _selectedLikesFriend.clear()
            _selectedDislikesFriend.clear()
        }
    }

    fun toggleGenre(genre: String, isDislike: Boolean) {
        val list = if (activePlayer == 1) {
            if (isDislike) _selectedDislikes else _selectedLikes
        } else {
            if (isDislike) _selectedDislikesFriend else _selectedLikesFriend
        }
        if (list.contains(genre)) list.remove(genre) else if (list.size < 3) list.add(genre)
    }

    fun nextDecade() {
        val currentDecadeValue = if (activePlayer == 1) selectedDecade else selectedDecadeFriend
        val currentIndex = decades.indexOf(currentDecadeValue)
        val nextIndex = (currentIndex + 1) % decades.size
        if (activePlayer == 1) selectedDecade = decades[nextIndex] else selectedDecadeFriend = decades[nextIndex]
    }

    fun prevDecade() {
        val currentDecadeValue = if (activePlayer == 1) selectedDecade else selectedDecadeFriend
        val currentIndex = decades.indexOf(currentDecadeValue)
        val prevIndex = if (currentIndex <= 0) decades.size - 1 else currentIndex - 1
        if (activePlayer == 1) selectedDecade = decades[prevIndex] else selectedDecadeFriend = decades[prevIndex]
    }

    fun selectCharacter(name: String) { selectedCharacterName = name }

    fun generateCharacterFullProfile() {
        val lang = currentLanguage.value
        val allGenres = GenresData.GENRES[lang] ?: GenresData.GENRES["en"]!!
        val shuffled = allGenres.shuffled()
        _selectedLikesFriend.clear()
        _selectedLikesFriend.addAll(shuffled.take(3))
        _selectedDislikesFriend.clear()
        _selectedDislikesFriend.addAll(shuffled.drop(3).take(3))
        selectedDecadeFriend = decades.random()
    }

    fun toggleMenu() { _isMenuOpen.value = !_isMenuOpen.value }
    fun closeMenu() { _isMenuOpen.value = false }
    fun showLockedError(message: String) {
        errorMessage = message
        viewModelScope.launch { delay(2000); errorMessage = null }
    }

    fun processMatches(navController: NavController) {
        val randomPage = (1..10).random()
        viewModelScope.launch {
            try {
                val type = selectedContentType.value ?: ContentType.Movie
                val allLikes = (selectedLikes + selectedLikesFriend).distinct()
                val allDislikes = (selectedDislikes + selectedDislikesFriend).distinct()
                val matchingGenres = allLikes.filter { it !in allDislikes }

                val genreIdsString = matchingGenres.mapNotNull { getGenreIdForApi(it, type) }.joinToString(",")

                val minYear = minOf(selectedDecade, selectedDecadeFriend)
                val maxYear = maxOf(selectedDecade, selectedDecadeFriend) + 9

                when (type) {
                    ContentType.Anime -> performJikanSearch(genreIdsString, minYear, maxYear, randomPage)
                    else -> performTmdbDiscover(type, genreIdsString, minYear, maxYear, randomPage)
                }
                if (navController.currentBackStackEntry?.destination?.route != ScreenNav.Match.route) {
                    navController.navigate(ScreenNav.Match.route)
                }
            } catch (e: Exception) { println("Search Error: ${e.message}") }
        }
    }

    private suspend fun performJikanSearch(genreIds: String, startYear: Int, endYear: Int, page: Int) {
        try {
            val response = RetrofitClient.instanceJikan.searchAnime(genres = if (genreIds.isEmpty()) null else genreIds, page = page)
            _items.value = response.data.map { it.toContentItem() }.filter { anime ->
                (anime.releaseDate.toIntOrNull() ?: 0) in startYear..endYear
            }
        } catch (e: Exception) { _items.value = emptyList() }
    }

    private suspend fun performTmdbDiscover(type: ContentType, genres: String, start: Int, end: Int, page: Int) {
        try {
            val response = if (type == ContentType.Movie) {
                RetrofitClient.instanceTmdb.getDiscoveryMovie(BuildConfig.TMDB_API_KEY, genres, "$start-01-01", "$end-12-31", page)
            } else {
                RetrofitClient.instanceTmdb.getDiscoveryTV(BuildConfig.TMDB_API_KEY, genres, "$start-01-01", "$end-12-31")
            }
            _items.value = response.results.map { it.toContentItem() }
        } catch (e: Exception) { _items.value = emptyList() }
    }

    private fun getGenreIdForApi(selectedGenre: String, type: ContentType): Int? {
        // Ищем индекс жанра в любом из языковых списков
        var foundIndex = -1
        for (langList in GenresData.GENRES.values) {
            val index = langList.indexOf(selectedGenre.trim())
            if (index != -1) {
                foundIndex = index
                break
            }
        }
        
        if (foundIndex == -1) return null

        // Берем английское название по этому индексу для сопоставления с ID
        val englishName = GenresData.GENRES["en"]?.get(foundIndex) ?: return null

        return if (type == ContentType.Anime) GenresData.jikanGenreIds[englishName] else GenresData.tmdbGenreIds[englishName]
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
            } catch (e: Exception) {}
        }
    }
}
