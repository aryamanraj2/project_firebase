package com.nsutrack.project_firebase.data.models

import com.google.gson.annotations.SerializedName

/**
 * Data class representing a cast member from TMDB API.
 */
data class Cast(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("character")
    val character: String?,

    @SerializedName("profile_path")
    val profilePath: String?,

    @SerializedName("order")
    val order: Int,

    @SerializedName("known_for_department")
    val knownForDepartment: String?
) {
    /**
     * Returns the full profile image URL for TMDB images.
     * @param size Image size (w45, w185, h632, original)
     */
    fun getProfileUrl(size: String = "w185"): String? {
        return profilePath?.let { "https://image.tmdb.org/t/p/$size$it" }
    }
}

/**
 * Crew member data class (for directors, writers, etc.)
 */
data class Crew(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("job")
    val job: String,

    @SerializedName("department")
    val department: String,

    @SerializedName("profile_path")
    val profilePath: String?
) {
    fun getProfileUrl(size: String = "w185"): String? {
        return profilePath?.let { "https://image.tmdb.org/t/p/$size$it" }
    }
}

/**
 * Response wrapper for movie credits endpoint
 */
data class CreditsResponse(
    @SerializedName("id")
    val movieId: Int,

    @SerializedName("cast")
    val cast: List<Cast>,

    @SerializedName("crew")
    val crew: List<Crew>
)
