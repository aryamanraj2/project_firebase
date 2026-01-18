package com.nsutrack.project_firebase

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.nsutrack.project_firebase.data.repository.SavedMoviesManager
import com.nsutrack.project_firebase.ui.theme.ProjectFirebaseTheme

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<android.content.Intent>
    private var onGoogleSignInSuccess: (String, String, String) -> Unit = { _, _, _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SavedMoviesManager
        SavedMoviesManager.init(applicationContext)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        
        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleGoogleSignInResult(task)
        }
        
        setContent {
            ProjectFirebaseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }

    private fun signUpWithEmail(email: String, password: String, name: String, age: String, gender: String, onSuccess: (String, String, String) -> Unit) {
        if (email.isEmpty() || password.isEmpty()) {
            showToast("Email and password cannot be empty")
            return
        }
        
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    saveUserProfile(userId, name, email, age, gender)
                    showToast("Registration successful!")
                    onSuccess(name, email, gender)
                } else {
                    showToast(getAuthErrorMessage(task.exception))
                }
            }
    }

    private fun saveUserProfile(userId: String, name: String, email: String, age: String, gender: String) {
        val profile = hashMapOf(
            "name" to name,
            "email" to email,
            "age" to age,
            "gender" to gender
        )
        db.collection("users").document(userId).set(profile)
            .addOnFailureListener { e ->
                showToast("Profile save failed: ${e.message}")
            }
    }

    private fun signInWithEmail(email: String, password: String, onSuccess: (String, String, String) -> Unit) {
        if (email.isEmpty() || password.isEmpty()) {
            showToast("Email and password cannot be empty")
            return
        }
        
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    loadUserProfile(email, onSuccess)
                } else {
                    showToast(getAuthErrorMessage(task.exception))
                }
            }
    }

    private fun loadUserProfile(fallbackEmail: String, onSuccess: (String, String, String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val name = doc.getString("name") ?: "User"
                    val email = doc.getString("email") ?: fallbackEmail
                    val gender = doc.getString("gender") ?: "Not Specified"
                    showToast("Login successful!")
                    onSuccess(name, email, gender)
                } else {
                    showToast("Profile not found")
                    onSuccess("User", fallbackEmail, "Not Specified")
                }
            }
            .addOnFailureListener {
                showToast("Failed to load profile")
                onSuccess("User", fallbackEmail, "Not Specified")
            }
    }
    private fun signOut() {
        auth.signOut()
        googleSignInClient.signOut()
    }

    private fun signInWithGoogle() {
        googleSignInLauncher.launch(googleSignInClient.signInIntent)
    }
    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            showToast("Google Sign-In failed")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    handleGoogleAuthSuccess()
                } else {
                    showToast("Firebase authentication failed")
                }
            }
    }

    private fun handleGoogleAuthSuccess() {
        val user = auth.currentUser ?: return
        val userId = user.uid
        val userEmail = user.email ?: ""
        val userName = user.displayName ?: "User"
        
        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    // Existing user
                    val name = doc.getString("name") ?: userName
                    val email = doc.getString("email") ?: userEmail
                    val gender = doc.getString("gender") ?: "Not Specified"
                    showToast("Welcome back, $name!")
                    onGoogleSignInSuccess(name, email, gender)
                } else {
                    // New user - create profile
                    val profile = hashMapOf(
                        "name" to userName,
                        "email" to userEmail,
                        "age" to "Not Specified",
                        "gender" to "Not Specified"
                    )
                    db.collection("users").document(userId).set(profile)
                    showToast("Welcome, $userName!")
                    onGoogleSignInSuccess(userName, userEmail, "Not Specified")
                }
            }
            .addOnFailureListener {
                showToast("Failed to load profile")
                onGoogleSignInSuccess(userName, userEmail, "Not Specified")
            }
    }
    

    private fun sendPasswordResetEmail(email: String) {
        if (email.isEmpty()) {
            showToast("Email cannot be empty")
            return
        }
        
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showToast("Password reset email sent! Check your inbox.")
                } else {
                    showToast(getAuthErrorMessage(task.exception))
                }
            }
    }

    // Helper functions
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun getAuthErrorMessage(exception: Exception?): String {
        return when {
            exception?.message?.contains("email address is already in use") == true -> "Email already in use"
            exception?.message?.contains("password") == true -> "Wrong password"
            exception?.message?.contains("no user record") == true -> "No account found"
            else -> "Authentication failed"
        }
    }

    @Composable
    fun AppNavigation() {
        var currentScreen by remember { mutableStateOf(Screen.Landing) }
        var currentUser by remember { mutableStateOf(Triple("User", "email@example.com", "Not Specified")) }
        var isCheckingSession by remember { mutableStateOf(true) }

        // Check for existing session on startup
        LaunchedEffect(Unit) {
            val user = auth.currentUser
            if (user != null) {
                val userId = user.uid
                val userEmail = user.email ?: ""
                val userName = user.displayName ?: "User"
                
                db.collection("users").document(userId).get()
                    .addOnSuccessListener { doc ->
                        currentUser = if (doc.exists()) {
                            Triple(
                                doc.getString("name") ?: userName,
                                doc.getString("email") ?: userEmail,
                                doc.getString("gender") ?: "Not Specified"
                            )
                        } else {
                            Triple(userName, userEmail, "Not Specified")
                        }
                        currentScreen = Screen.Home
                        isCheckingSession = false
                    }
                    .addOnFailureListener {
                        currentUser = Triple(userName, userEmail, "Not Specified")
                        currentScreen = Screen.Home
                        isCheckingSession = false
                    }
            } else {
                isCheckingSession = false
            }
        }

        onGoogleSignInSuccess = { name, email, gender ->
            currentUser = Triple(name, email, gender)
            currentScreen = Screen.Home
        }

        if (isCheckingSession) return

        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                val animationSpec = tween<IntOffset>(durationMillis = 300)
                if (targetState.ordinal > initialState.ordinal) {
                    (slideInHorizontally(animationSpec = animationSpec) { width -> width } + fadeIn()).togetherWith(
                        slideOutHorizontally(animationSpec = animationSpec) { width -> -width } + fadeOut())
                } else {
                    (slideInHorizontally(animationSpec = animationSpec) { width -> -width } + fadeIn()).togetherWith(
                        slideOutHorizontally(animationSpec = animationSpec) { width -> width } + fadeOut())
                }
            },
            label = "Page Transition"
        ) { targetScreen ->
            when (targetScreen) {
                Screen.Landing -> {
                    LandingScreen(
                        onSignInClick = { currentScreen = Screen.SignIn },
                        onSignUpClick = { currentScreen = Screen.SignUp },
                        onGoogleSignInClick = {
                            signInWithGoogle()
                        },
                        onDevBypassClick = {
                            // DEV BYPASS - Skip auth and go to Home with test user
                            currentUser = Triple("Test User", "dev@test.com", "Not Specified")
                            currentScreen = Screen.Home
                        }
                    )
                }
                Screen.SignIn -> {
                    SignInScreen(
                        onBackClick = { currentScreen = Screen.Landing },
                        onSignInClick = { email, password ->
                            signInWithEmail(email, password) { name, userEmail, gender ->
                                currentUser = Triple(name, userEmail, gender)
                                currentScreen = Screen.Home
                            }
                        },
                        onForgotPasswordClick = { email ->
                            sendPasswordResetEmail(email)
                        },
                        onGoogleSignInClick = {
                            signInWithGoogle()
                        }
                    )
                }
                Screen.SignUp -> {
                    SignUpScreen(
                        onBackClick = { currentScreen = Screen.Landing },
                        onSignUpClick = { name, age, gender, email, password ->
                            signUpWithEmail(email, password, name, age, gender) { userName, userEmail, userGender ->
                                currentUser = Triple(userName, userEmail, userGender)
                                currentScreen = Screen.Home
                            }
                        },
                        onGoogleSignInClick = {
                            signInWithGoogle()
                        }
                    )
                }
                Screen.Home -> {
                    HomeScreen(
                        userName = currentUser.first,
                        userEmail = currentUser.second,
                        userGender = currentUser.third,
                        onSignOutClick = {
                            signOut()
                            currentScreen = Screen.Landing
                        }
                    )
                }
            }
        }
    }
}

enum class Screen {
    Landing,
    SignIn,
    SignUp,
    Home
}
