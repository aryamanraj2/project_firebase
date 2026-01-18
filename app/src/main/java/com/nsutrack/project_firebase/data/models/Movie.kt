package com.nsutrack.project_firebase.data.models

import com.google.gson.annotations.SerializedName

/**
 * Data class representing a Movie from TMDB API.
 * Maps to the JSON structure returned by TMDB endpoints.
 */
data class Movie(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("original_title")
    val originalTitle: String? = null,

    @SerializedName("overview")
    val overview: String,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("backdrop_path")
    val backdropPath: String?,

    @SerializedName("release_date")
    val releaseDate: String? = null,

    @SerializedName("vote_average")
    val voteAverage: Double,

    @SerializedName("vote_count")
    val voteCount: Int,

    @SerializedName("popularity")
    val popularity: Double,

    @SerializedName("adult")
    val adult: Boolean = false,

    @SerializedName("genre_ids")
    val genreIds: List<Int> = emptyList(),

    @SerializedName("original_language")
    val originalLanguage: String? = null,

    @SerializedName("video")
    val video: Boolean = false
) {
    /**
     * Returns the full poster URL for TMDB images.
     * @param size Image size (w185, w342, w500, w780, original)
     */
    fun getPosterUrl(size: String = "w500"): String? {
        return posterPath?.let { "https://image.tmdb.org/t/p/$size$it" }
    }

    /**
     * Returns the full backdrop URL for TMDB images.
     * @param size Image size (w300, w780, w1280, original)
     */
    fun getBackdropUrl(size: String = "w1280"): String? {
        return backdropPath?.let { "https://image.tmdb.org/t/p/$size$it" }
    }

    /**
     * Returns the year extracted from release date.
     */
    fun getReleaseYear(): String? {
        return releaseDate?.take(4)
    }

    /**
     * Returns formatted rating (e.g., "8.5")
     */
    fun getFormattedRating(): String {
        return String.format("%.1f", voteAverage)
    }
}

/**
 * Response wrapper for movie list endpoints (popular, trending, etc.)
 */
data class MovieListResponse(
    @SerializedName("page")
    val page: Int,

    @SerializedName("results")
    val results: List<Movie>,

    @SerializedName("total_pages")
    val totalPages: Int,

    @SerializedName("total_results")
    val totalResults: Int
)

/**
 * Detailed movie information including runtime, genres, etc.
 * Used for the Details screen.
 */
data class MovieDetails(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("overview")
    val overview: String,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("backdrop_path")
    val backdropPath: String?,

    @SerializedName("release_date")
    val releaseDate: String?,

    @SerializedName("vote_average")
    val voteAverage: Double,

    @SerializedName("runtime")
    val runtime: Int?,

    @SerializedName("genres")
    val genres: List<Genre>,

    @SerializedName("tagline")
    val tagline: String?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("budget")
    val budget: Long?,

    @SerializedName("revenue")
    val revenue: Long?
) {
    fun getPosterUrl(size: String = "w500"): String? {
        return posterPath?.let { "https://image.tmdb.org/t/p/$size$it" }
    }

    fun getBackdropUrl(size: String = "w1280"): String? {
        return backdropPath?.let { "https://image.tmdb.org/t/p/$size$it" }
    }

    fun getReleaseYear(): String? {
        return releaseDate?.take(4)
    }

    fun getFormattedRating(): String {
        return String.format("%.1f", voteAverage)
    }

    /**
     * Format runtime to hours and minutes (e.g., "2h 10m")
     */
    fun getFormattedRuntime(): String? {
        return runtime?.let {
            val hours = it / 60
            val minutes = it % 60
            if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        }
    }
}

/**
 * Genre data class
 */
data class Genre(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String
)
