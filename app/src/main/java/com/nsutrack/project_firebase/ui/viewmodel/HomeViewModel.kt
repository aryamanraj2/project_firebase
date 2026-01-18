package com.nsutrack.project_firebase.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nsutrack.project_firebase.data.models.Movie
import com.nsutrack.project_firebase.data.repository.MovieRepository
import com.nsutrack.project_firebase.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the Home screen
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val heroMovie: Movie? = null,
    val trendingMovies: List<Movie> = emptyList(),
    val popularMovies: List<Movie> = emptyList(),
    val tvSeries: List<Movie> = emptyList(),
    val koreanDramas: List<Movie> = emptyList(),
    val error: String? = null
)

/**
 * ViewModel for the Home screen.
 * Manages UI state and fetches movie data from the repository.
 */
class HomeViewModel(
    private val repository: MovieRepository = MovieRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    /**
     * Load all data for the home screen
     */
    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Launch all API calls concurrently
            launch { loadTrendingMovies() }
            launch { loadPopularMovies() }
            launch { loadTVSeries() }
            launch { loadKoreanDramas() }
        }
    }

    private suspend fun loadTrendingMovies() {
        when (val result = repository.getTrendingMovies()) {
            is Result.Success -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        trendingMovies = result.data,
                        heroMovie = currentState.heroMovie ?: result.data.firstOrNull(),
                        isLoading = false
                    )
                }
            }
            is Result.Error -> {
                _uiState.update { it.copy(error = result.message, isLoading = false) }
            }
            is Result.Loading -> { /* No-op */ }
        }
    }

    private suspend fun loadPopularMovies() {
        when (val result = repository.getPopularMovies()) {
            is Result.Success -> {
                _uiState.update { it.copy(popularMovies = result.data, isLoading = false) }
            }
            is Result.Error -> {
                _uiState.update { it.copy(error = result.message, isLoading = false) }
            }
            is Result.Loading -> { /* No-op */ }
        }
    }

    private suspend fun loadTVSeries() {
        when (val result = repository.getPopularTVSeries()) {
            is Result.Success -> {
                _uiState.update { it.copy(tvSeries = result.data, isLoading = false) }
            }
            is Result.Error -> {
                _uiState.update { it.copy(error = result.message, isLoading = false) }
            }
            is Result.Loading -> { /* No-op */ }
        }
    }

    private suspend fun loadKoreanDramas() {
        when (val result = repository.getKoreanDramas()) {
            is Result.Success -> {
                _uiState.update { it.copy(koreanDramas = result.data, isLoading = false) }
            }
            is Result.Error -> {
                _uiState.update { it.copy(error = result.message, isLoading = false) }
            }
            is Result.Loading -> { /* No-op */ }
        }
    }

    /**
     * Set the hero movie (featured movie at the top)
     */
    fun setHeroMovie(movie: Movie) {
        _uiState.update { it.copy(heroMovie = movie) }
    }

    /**
     * Clear any error state
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Refresh all home data
     */
    fun refresh() {
        loadHomeData()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(MovieRepository.getInstance()) as T
            }
        }
    }
}
