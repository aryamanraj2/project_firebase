package com.nsutrack.project_firebase

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nsutrack.project_firebase.ui.theme.ProjectFirebaseTheme

@Composable
fun HomeScreen(
    userName: String = "User",
    userEmail: String = "user@example.com",
    userGender: String = "Not Specified",
    onSignOutClick: () -> Unit = {}
) {
    var showProfile by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Hello!",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            // Profile button in top-left
            IconButton(
                onClick = { showProfile = true },
                modifier = Modifier
                    .padding(padding)
                    .padding(8.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Profile dialog
    if (showProfile) {
        AlertDialog(
            onDismissRequest = { showProfile = false },
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
                Button(onClick = {
                    showProfile = false
                    onSignOutClick()
                }) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfile = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ProjectFirebaseTheme {
        HomeScreen(
            userName = "John Doe",
            userEmail = "john.doe@example.com",
            userGender = "Male"
        )
    }
}
