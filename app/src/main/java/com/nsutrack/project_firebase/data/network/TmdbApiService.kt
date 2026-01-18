package com.nsutrack.project_firebase.data.network

import com.nsutrack.project_firebase.BuildConfig
import com.nsutrack.project_firebase.data.models.CreditsResponse
import com.nsutrack.project_firebase.data.models.MovieDetails
import com.nsutrack.project_firebase.data.models.MovieListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for TMDB API endpoints.
 * Base URL: https://api.themoviedb.org/3/
 */
interface TmdbApiService {

    /**
     * Get popular movies
     */
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MovieListResponse

    /**
     * Get trending movies (day or week)
     */
    @GET("trending/movie/{time_window}")
    suspend fun getTrendingMovies(
        @Path("time_window") timeWindow: String = "day",
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MovieListResponse

    /**
     * Get now playing movies
     */
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MovieListResponse

    /**
     * Get top rated movies
     */
    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MovieListResponse

    /**
     * Get upcoming movies
     */
    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MovieListResponse

    /**
     * Get movie details by ID
     */
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US"
    ): MovieDetails

    /**
     * Get movie credits (cast and crew)
     */
    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US"
    ): CreditsResponse

    /**
     * Search movies by query
     */
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false
    ): MovieListResponse

    /**
     * Discover movies with filters
     */
    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("with_genres") withGenres: String? = null,
        @Query("with_original_language") withOriginalLanguage: String? = null
    ): MovieListResponse

    /**
     * Get Korean TV dramas (K-Drama)
     * Uses discover with Korean language filter
     */
    @GET("discover/tv")
    suspend fun getKoreanDramas(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("with_original_language") withOriginalLanguage: String = "ko"
    ): MovieListResponse

    /**
     * Get popular TV series
     */
    @GET("tv/popular")
    suspend fun getPopularTVSeries(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MovieListResponse

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"

        val API_KEY: String = BuildConfig.TMDB_API_KEY

        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
    }
}
