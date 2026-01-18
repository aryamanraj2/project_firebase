package com.nsutrack.project_firebase.data.repository

import com.nsutrack.project_firebase.data.models.CreditsResponse
import com.nsutrack.project_firebase.data.models.Movie
import com.nsutrack.project_firebase.data.models.MovieDetails
import com.nsutrack.project_firebase.data.network.NetworkModule
import com.nsutrack.project_firebase.data.network.TmdbApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Sealed class representing the result of a network operation.
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable, val message: String? = null) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}

/**
 * Repository for movie data operations.
 * Abstracts the data source (TMDB API) from the ViewModel.
 */
class MovieRepository(
    private val apiService: TmdbApiService = NetworkModule.tmdbApiService
) {

    /**
     * Fetch trending movies (daily)
     */
    suspend fun getTrendingMovies(page: Int = 1): Result<List<Movie>> {
        return safeApiCall {
            apiService.getTrendingMovies(timeWindow = "day", page = page).results
        }
    }

    /**
     * Fetch popular movies
     */
    suspend fun getPopularMovies(page: Int = 1): Result<List<Movie>> {
        return safeApiCall {
            apiService.getPopularMovies(page = page).results
        }
    }

    /**
     * Fetch now playing movies
     */
    suspend fun getNowPlayingMovies(page: Int = 1): Result<List<Movie>> {
        return safeApiCall {
            apiService.getNowPlayingMovies(page = page).results
        }
    }

    /**
     * Fetch top rated movies
     */
    suspend fun getTopRatedMovies(page: Int = 1): Result<List<Movie>> {
        return safeApiCall {
            apiService.getTopRatedMovies(page = page).results
        }
    }

    /**
     * Fetch upcoming movies
     */
    suspend fun getUpcomingMovies(page: Int = 1): Result<List<Movie>> {
        return safeApiCall {
            apiService.getUpcomingMovies(page = page).results
        }
    }

    /**
     * Fetch popular TV series
     */
    suspend fun getPopularTVSeries(page: Int = 1): Result<List<Movie>> {
        return safeApiCall {
            apiService.getPopularTVSeries(page = page).results
        }
    }

    /**
     * Fetch Korean dramas
     */
    suspend fun getKoreanDramas(page: Int = 1): Result<List<Movie>> {
        return safeApiCall {
            apiService.getKoreanDramas(page = page).results
        }
    }

    /**
     * Fetch movie details by ID
     */
    suspend fun getMovieDetails(movieId: Int): Result<MovieDetails> {
        return safeApiCall {
            apiService.getMovieDetails(movieId = movieId)
        }
    }

    /**
     * Fetch movie credits (cast and crew)
     */
    suspend fun getMovieCredits(movieId: Int): Result<CreditsResponse> {
        return safeApiCall {
            apiService.getMovieCredits(movieId = movieId)
        }
    }

    /**
     * Search movies by query
     */
    suspend fun searchMovies(query: String, page: Int = 1): Result<List<Movie>> {
        return safeApiCall {
            apiService.searchMovies(query = query, page = page).results
        }
    }

    /**
     * Discover movies with genre filter
     */
    suspend fun discoverMoviesByGenre(genreId: Int, page: Int = 1): Result<List<Movie>> {
        return safeApiCall {
            apiService.discoverMovies(
                withGenres = genreId.toString(),
                page = page
            ).results
        }
    }

    /**
     * Safe API call wrapper that handles exceptions
     */
    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                Result.Success(apiCall())
            } catch (e: Exception) {
                Result.Error(
                    exception = e,
                    message = e.localizedMessage ?: "An unexpected error occurred"
                )
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: MovieRepository? = null

        fun getInstance(): MovieRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MovieRepository().also { INSTANCE = it }
            }
        }
    }
}
