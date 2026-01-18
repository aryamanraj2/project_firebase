package com.nsutrack.project_firebase

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nsutrack.project_firebase.data.models.Movie
import com.nsutrack.project_firebase.ui.components.BottomNavBar
import com.nsutrack.project_firebase.ui.components.BottomNavItem
import com.nsutrack.project_firebase.ui.components.HeroSection
import com.nsutrack.project_firebase.ui.components.MovieRow
import com.nsutrack.project_firebase.ui.screens.MovieDetailScreen
import com.nsutrack.project_firebase.ui.screens.SavedScreen
import com.nsutrack.project_firebase.ui.screens.SearchScreen
import com.nsutrack.project_firebase.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    userName: String = "User",
    userEmail: String = "user@example.com",
    userGender: String = "Not Specified",
    onSignOutClick: () -> Unit = {}
) {
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showProfile by remember { mutableStateOf(false) }
    var selectedNavItem by remember { mutableStateOf(BottomNavItem.Home) }
    var selectedMovie by remember { mutableStateOf<Movie?>(null) }

    val darkBackground = Color(0xFF0A0A0A)

    // Show detail screen when a movie is selected
    if (selectedMovie != null) {
        MovieDetailScreen(
            movie = selectedMovie!!,
            onBackClick = { selectedMovie = null }
        )
        return
    }

    Scaffold(
        containerColor = darkBackground,
        bottomBar = {
            BottomNavBar(
                selectedItem = selectedNavItem,
                onItemSelected = { selectedNavItem = it }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(darkBackground)
        ) {
            // Content based on selected nav item
            AnimatedContent(
                targetState = selectedNavItem,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "nav_content"
            ) { navItem ->
                when (navItem) {
                    BottomNavItem.Home -> {
                        HomeContent(
                            uiState = uiState,
                            padding = padding,
                            onMovieClick = { movie -> selectedMovie = movie },
                            onRefresh = { viewModel.refresh() }
                        )
                    }
                    BottomNavItem.Search -> {
                        SearchScreen(
                            onMovieClick = { movie -> selectedMovie = movie },
                            modifier = Modifier.padding(bottom = padding.calculateBottomPadding())
                        )
                    }
                    BottomNavItem.Saved -> {
                        SavedScreen(
                            onMovieClick = { movie -> selectedMovie = movie },
                            modifier = Modifier.padding(bottom = padding.calculateBottomPadding())
                        )
                    }
                }
            }

            // Top bar (only show on Home tab)
            if (selectedNavItem == BottomNavItem.Home) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showProfile = true }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    IconButton(onClick = { selectedNavItem = BottomNavItem.Search }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }

    // Profile dialog
    if (showProfile) {
        AlertDialog(
            onDismissRequest = { showProfile = false },
            containerColor = Color(0xFF1A1A1A),
            titleContentColor = Color.White,
            textContentColor = Color.White,
            title = { Text("Profile") },
            text = {
                Column {
                    Text("Name: $userName")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Email: $userEmail")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Gender: $userGender")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showProfile = false
                        onSignOutClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfile = false }) {
                    Text("Close", color = Color.White)
                }
            }
        )
    }
}

@Composable
private fun HomeContent(
    uiState: com.nsutrack.project_firebase.ui.viewmodel.HomeUiState,
    padding: PaddingValues,
    onMovieClick: (Movie) -> Unit,
    onRefresh: () -> Unit
) {
    val darkBackground = Color(0xFF0A0A0A)

    if (uiState.isLoading && uiState.trendingMovies.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    } else if (uiState.error != null && uiState.trendingMovies.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Failed to load movies",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.error ?: "Unknown error",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRefresh,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text("Retry")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Make sure you've added your TMDB API key",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = padding.calculateBottomPadding())
        ) {
            // Hero Section
            item {
                HeroSection(
                    movie = uiState.heroMovie,
                    onPlayClick = { movie -> onMovieClick(movie) },
                    onDetailsClick = { movie -> onMovieClick(movie) }
                )
            }

            // Trending Now
            if (uiState.trendingMovies.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    MovieRow(
                        title = "Trending Now",
                        movies = uiState.trendingMovies,
                        onMovieClick = onMovieClick,
                        onSeeAllClick = { }
                    )
                }
            }

            // Popular Movies
            if (uiState.popularMovies.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    MovieRow(
                        title = "Popular on Streamflix",
                        movies = uiState.popularMovies,
                        onMovieClick = onMovieClick,
                        onSeeAllClick = { }
                    )
                }
            }

            // TV Series
            if (uiState.tvSeries.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    MovieRow(
                        title = "Series",
                        movies = uiState.tvSeries,
                        onMovieClick = onMovieClick,
                        onSeeAllClick = { }
                    )
                }
            }

            // Korean TV Drama
            if (uiState.koreanDramas.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    MovieRow(
                        title = "Korean TV Drama",
                        movies = uiState.koreanDramas,
                        onMovieClick = onMovieClick,
                        onSeeAllClick = { }
                    )
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
