package com.nsutrack.project_firebase.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nsutrack.project_firebase.data.models.Movie

/**
 * Horizontal scrollable row of movie cards with section header.
 * Standard Netflix-style content row.
 *
 * @param title Section title
 * @param movies List of movies to display
 * @param onMovieClick Callback when a movie card is clicked
 * @param onSeeAllClick Optional callback for "See All" action
 * @param cardWidth Width of each movie card
 * @param modifier Optional modifier
 */
@Composable
fun MovieRow(
    title: String,
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit,
    onSeeAllClick: (() -> Unit)? = null,
    cardWidth: Dp = 140.dp,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            title = title,
            onSeeAllClick = onSeeAllClick
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = movies,
                key = { movie -> movie.id }
            ) { movie ->
                MovieCard(
                    movie = movie,
                    onClick = onMovieClick,
                    width = cardWidth
                )
            }
        }
    }
}

/**
 * Horizontal scrollable row with backdrop-style cards.
 * Good for featured content or "Continue Watching" sections.
 *
 * @param title Section title
 * @param movies List of movies to display
 * @param onMovieClick Callback when a movie card is clicked
 * @param onSeeAllClick Optional callback for "See All" action
 * @param modifier Optional modifier
 */
@Composable
fun MovieBackdropRow(
    title: String,
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit,
    onSeeAllClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            title = title,
            onSeeAllClick = onSeeAllClick
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = movies,
                key = { movie -> movie.id }
            ) { movie ->
                MovieBackdropCard(
                    movie = movie,
                    onClick = onMovieClick
                )
            }
        }
    }
}

/**
 * Movie row with larger cards, suitable for featured sections.
 */
@Composable
fun MovieRowLarge(
    title: String,
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit,
    onSeeAllClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    MovieRow(
        title = title,
        movies = movies,
        onMovieClick = onMovieClick,
        onSeeAllClick = onSeeAllClick,
        cardWidth = 180.dp,
        modifier = modifier
    )
}
