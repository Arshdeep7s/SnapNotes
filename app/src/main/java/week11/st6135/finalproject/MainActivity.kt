package week11.st6135.finalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import week11.st6135.finalproject.viewmodel.AuthViewModel
import week11.st6135.finalproject.screens.LoginScreen
import week11.st6135.finalproject.screens.RegisterScreen
import week11.st6135.finalproject.screens.ForgotPasswordScreen
import week11.st6135.finalproject.screens.HomeScreen
import week11.st6135.finalproject.screens.NotesListScreen
import week11.st6135.finalproject.screens.AddNoteScreen
import week11.st6135.finalproject.util.AuthState
import week11.st6135.finalproject.viewmodel.NotesViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import week11.st6135.finalproject.ui.theme.AppTypography

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val notesViewModel: NotesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF53103B),  // Button color
                    onPrimary = Color.White
                ),
                typography = AppTypography){
                Surface {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = NavRoutes.SPLASH
                    ) {
                        //SPLASH SCREEN
                        composable(NavRoutes.SPLASH) {
                            SplashScreen(
                                viewModel = authViewModel,
                                onFinished = { isLoggedIn ->
                                    if (isLoggedIn) {
                                        navController.navigate(NavRoutes.HOME) {
                                            popUpTo(NavRoutes.SPLASH) { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate(NavRoutes.LOGIN) {
                                            popUpTo(NavRoutes.SPLASH) { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }

                        // LOGIN SCREEN
                        composable(NavRoutes.LOGIN) {
                            LoginScreen(
                                viewModel = authViewModel,
                                onRegisterClick = { navController.navigate(NavRoutes.REGISTER) },
                                onForgotPasswordClick = { navController.navigate(NavRoutes.FORGOT) },
                                onLoggedIn = { navController.navigate(NavRoutes.HOME) }
                            )
                        }

                        // REGISTER SCREEN
                        composable(NavRoutes.REGISTER) {
                            RegisterScreen(
                                viewModel = authViewModel,
                                onBackToLogin = { navController.popBackStack() },
                                onRegisterSuccess = { navController.navigate(NavRoutes.HOME) }
                            )
                        }

                        // FORGOT PASSWORD SCREEN
                        composable(NavRoutes.FORGOT) {
                            ForgotPasswordScreen(
                                viewModel = authViewModel,
                                onBackToLogin = { navController.popBackStack() }
                            )
                        }

                        // HOME SCREEN
                        composable(NavRoutes.HOME) {
                            val userId = authViewModel.currentUser()?.uid ?: ""

                            HomeScreen(
                                onViewNotes = { navController.navigate(NavRoutes.NOTES_LIST) },
                                onAddNote = { navController.navigate(NavRoutes.ADD_NOTE) },
                                onLogout = {
                                    authViewModel.logout()
                                    navController.navigate(NavRoutes.LOGIN) {
                                        popUpTo(NavRoutes.HOME) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // NOTES LIST SCREEN
                        composable(NavRoutes.NOTES_LIST) {
                            val userId = authViewModel.currentUser()?.uid ?: ""

                            NotesListScreen(
                                userId = userId,
                                notesViewModel = notesViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // ADD NOTE SCREEN
                        composable(NavRoutes.ADD_NOTE) {
                            val userId = authViewModel.currentUser()?.uid ?: ""

                            AddNoteScreen(
                                userId = userId,
                                notesViewModel = notesViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(
    onFinished: (Boolean) -> Unit, // true = user logged in
    viewModel: AuthViewModel
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        val user = viewModel.currentUser()
        onFinished(user != null)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
