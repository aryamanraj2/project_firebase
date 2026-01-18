package com.nsutrack.project_firebase.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.nsutrack.project_firebase.data.models.Movie

/**
 * Movie poster card component for displaying movies in horizontal rows.
 * Uses Coil for async image loading with loading state.
 *
 * @param movie The movie data to display
 * @param onClick Callback when the card is clicked
 * @param modifier Optional modifier for the card
 * @param width Width of the card (default 140.dp for standard row cards)
 */
@Composable
fun MovieCard(
    movie: Movie,
    onClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 140.dp
) {
    Card(
        modifier = modifier
            .width(width)
            .aspectRatio(2f / 3f) // Standard movie poster ratio
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick(movie) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(movie.getPosterUrl())
                .crossfade(true)
                .build(),
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            },
            error = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Placeholder for failed image load
                    androidx.compose.material3.Text(
                        text = movie.title.take(1),
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )
                }
            }
        )
    }
}

/**
 * Larger movie card for featured sections or grid layouts.
 */
@Composable
fun MovieCardLarge(
    movie: Movie,
    onClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 180.dp
) {
    MovieCard(
        movie = movie,
        onClick = onClick,
        modifier = modifier,
        width = width
    )
}

/**
 * Backdrop-style card for horizontal movie previews.
 * Uses backdrop image instead of poster (16:9 ratio).
 */
@Composable
fun MovieBackdropCard(
    movie: Movie,
    onClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 280.dp
) {
    Card(
        modifier = modifier
            .width(width)
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick(movie) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(movie.getBackdropUrl())
                .crossfade(true)
                .build(),
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        )
    }
}
