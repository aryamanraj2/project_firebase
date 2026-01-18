package com.nsutrack.project_firebase.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nsutrack.project_firebase.data.models.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manager for handling saved/bookmarked movies.
 * Uses SharedPreferences for persistence.
 */
object SavedMoviesManager {
    private const val PREFS_NAME = "saved_movies_prefs"
    private const val KEY_SAVED_MOVIES = "saved_movies"

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    private val _savedMovies = MutableStateFlow<List<Movie>>(emptyList())
    val savedMovies: StateFlow<List<Movie>> = _savedMovies.asStateFlow()

    private val _savedMovieIds = MutableStateFlow<Set<Int>>(emptySet())
    val savedMovieIds: StateFlow<Set<Int>> = _savedMovieIds.asStateFlow()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadSavedMovies()
    }

    private fun loadSavedMovies() {
        val json = prefs.getString(KEY_SAVED_MOVIES, null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<Movie>>() {}.type
                val movies: List<Movie> = gson.fromJson(json, type)
                _savedMovies.value = movies
                _savedMovieIds.value = movies.map { it.id }.toSet()
            } catch (e: Exception) {
                _savedMovies.value = emptyList()
                _savedMovieIds.value = emptySet()
            }
        }
    }

    private fun saveToPersistence() {
        val json = gson.toJson(_savedMovies.value)
        prefs.edit().putString(KEY_SAVED_MOVIES, json).apply()
    }

    fun toggleSaved(movie: Movie) {
        val currentList = _savedMovies.value.toMutableList()
        val isCurrentlySaved = _savedMovieIds.value.contains(movie.id)

        if (isCurrentlySaved) {
            currentList.removeAll { it.id == movie.id }
        } else {
            currentList.add(0, movie)
        }

        _savedMovies.value = currentList
        _savedMovieIds.value = currentList.map { it.id }.toSet()
        saveToPersistence()
    }

    fun isMovieSaved(movieId: Int): Boolean {
        return _savedMovieIds.value.contains(movieId)
    }

    fun addMovie(movie: Movie) {
        if (!isMovieSaved(movie.id)) {
            val currentList = _savedMovies.value.toMutableList()
            currentList.add(0, movie)
            _savedMovies.value = currentList
            _savedMovieIds.value = currentList.map { it.id }.toSet()
            saveToPersistence()
        }
    }

    fun removeMovie(movieId: Int) {
        val currentList = _savedMovies.value.toMutableList()
        currentList.removeAll { it.id == movieId }
        _savedMovies.value = currentList
        _savedMovieIds.value = currentList.map { it.id }.toSet()
        saveToPersistence()
    }
}
