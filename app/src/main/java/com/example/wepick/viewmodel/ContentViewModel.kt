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
import com.example.wepick.util.Language
import com.example.wepick.util.Paging
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ContentViewModel : ViewModel() {
    private val _items = mutableStateOf<List<ContentItem>>(emptyList())
    val items: State<List<ContentItem>> = _items

    private var matchJob: Job? = null
    private var pool: List<ContentItem> = emptyList()
    private var batchStart: Int = 0

    private val _isMatching = mutableStateOf(false)
    val isMatching: State<Boolean> = _isMatching

    init {
        loadContent(ContentType.Movie, BuildConfig.TMDB_API_KEY)
        loadContent(ContentType.Tv, BuildConfig.TMDB_API_KEY)
        loadContent(ContentType.Anime, BuildConfig.JIKAN_BASE_URL)
    }

    private fun setPool(items: List<ContentItem>) {
        pool = items
        batchStart = 0
        emitCurrentBatch()
    }

    private fun emitCurrentBatch() {
        if (pool.isEmpty()) {
            _items.value = emptyList()
            return
        }
        val end = minOf(batchStart + Paging.BATCH_SIZE, pool.size)
        _items.value = pool.subList(batchStart, end)
    }

    fun showNextBatch() {
        if (pool.isEmpty()) return
        batchStart += Paging.BATCH_SIZE
        if (batchStart >= pool.size) batchStart = 0
        emitCurrentBatch()
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
        selectedLikes: List<Int>,
        selectedLikesFriend: List<Int>,
        selectedDislikes: List<Int>,
        selectedDislikesFriend: List<Int>,
        selectedDecade: Int,
        selectedDecadeFriend: Int,
        onDone: () -> Unit,
    ) {
        matchJob?.cancel()
        _isMatching.value = true
        matchJob = viewModelScope.launch {
            try {
                val genreIds = selectedLikes
                    .mapNotNull { index -> GenresData.GENRES[Language.EN]?.getOrNull(index) }
                    .mapNotNull { englishGenre ->
                        if (type == ContentType.Anime) GenresData.jikanGenreIds[englishGenre]
                        else GenresData.tmdbGenreIds[englishGenre]
                    }

                val minYear = minOf(selectedDecade, selectedDecadeFriend)
                val maxYear = maxOf(selectedDecade, selectedDecadeFriend) + 9

                when (type) {
                    ContentType.Anime -> performJikanSearch(genreIds.joinToString(","), minYear, maxYear)
                    else -> performTmdbDiscover(type, genreIds.joinToString("|"), minYear, maxYear)
                }
                onDone()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                println("Search Error: ${e.message}")
            } finally {
                _isMatching.value = false
            }
        }
    }

    private suspend fun performJikanSearch(
        genreIds: String,
        startYear: Int,
        endYear: Int
    ) {
        val startDate = "$startYear-01-01"
        val endDate = "$endYear-12-31"
        println("Jikan search: genres='$genreIds' dates=$startDate..$endDate")
        val results = mutableListOf<com.example.wepick.data.model.jikan.AnimeItem>()
        for (page in 1..Paging.MAX_PAGES) {
            if (page > 1) delay(Paging.JIKAN_DELAY_MS)
            try {
                val pageData = RetrofitClient.instanceJikan.searchAnime(
                    genres = genreIds.ifEmpty { null },
                    page = page,
                    startDate = startDate,
                    endDate = endDate,
                ).data
                results += pageData
                if (pageData.isEmpty()) break
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                println("Jikan page $page error: ${e.message}")
            }
        }
        val deduped = results.distinctBy { it.malId }.map { it.toContentItem() }
        println("Jikan search: raw=${results.size}, deduped=${deduped.size}")
        setPool(deduped)
    }

    private suspend fun performTmdbDiscover(
        type: ContentType,
        genres: String,
        start: Int,
        end: Int
    ) = coroutineScope {
        val responses = (1..Paging.MAX_PAGES).map { page ->
            async {
                try {
                    if (type == ContentType.Movie) {
                        RetrofitClient.instanceTmdb.getDiscoveryMovie(
                            apiKey = BuildConfig.TMDB_API_KEY,
                            genres = genres,
                            dateStart = "$start-01-01",
                            dateEnd = "$end-12-31",
                            page = page,
                        )
                    } else {
                        RetrofitClient.instanceTmdb.getDiscoveryTV(
                            apiKey = BuildConfig.TMDB_API_KEY,
                            genres = genres,
                            dateStart = "$start-01-01",
                            dateEnd = "$end-12-31",
                            page = page,
                        )
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    println("TMDB page $page error: ${e.message}")
                    null
                }
            }
        }.awaitAll().filterNotNull()
        setPool(
            responses
                .flatMap { it.results }
                .distinctBy { it.id }
                .map { it.toContentItem() }
        )
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
        matchJob?.cancel()
        pool = emptyList()
        batchStart = 0
        _items.value = emptyList()
        loadContent(ContentType.Movie, BuildConfig.TMDB_API_KEY)
        loadContent(ContentType.Tv, BuildConfig.TMDB_API_KEY)
        loadContent(ContentType.Anime, BuildConfig.JIKAN_BASE_URL)
    }
}