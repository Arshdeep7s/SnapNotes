package week11.st6135.finalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import week11.st6135.finalproject.NavRoutes
import week11.st6135.finalproject.viewmodel.AuthViewModel
import week11.st6135.finalproject.screens.LoginScreen
import week11.st6135.finalproject.screens.RegisterScreen
import week11.st6135.finalproject.screens.ForgotPasswordScreen
import week11.st6135.finalproject.screens.HomeScreen
import week11.st6135.finalproject.ui.screens.NotesListScreen
import week11.st6135.finalproject.util.AuthState
import week11.st6135.finalproject.viewmodel.NotesViewModel
import androidx.compose.runtime.collectAsState

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val notesViewModel: NotesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = NavRoutes.LOGIN
                    ) {

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
                            HomeScreen(
                                onViewNotes = { navController.navigate("notesList") }
                            )
                        }


                        composable("notesList") {
                            NotesListScreen(
                                userId = authViewModel.state.collectAsState().value.let {
                                    if (it is AuthState.Success) it.user.uid else ""
                                },
                                notesViewModel = notesViewModel,
                                onAddNote = { /* navigate to add note */ },
                                onOpenNote = { note -> /* navigate to note details */ }
                            )
                        }

                    }
                }
            }
        }
    }
}
