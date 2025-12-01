package week11.st6135.finalproject.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import week11.st6135.finalproject.util.AuthState
import week11.st6135.finalproject.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onBackToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Navigate when registration is successful
    LaunchedEffect(state) {
        if (state is AuthState.Success) {
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Same style as Login screen
        Text(
            "Register",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 38.sp,
                color = Color(0xFFA84E4E)
            )
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password (min 6 chars)") },
            singleLine = true
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { viewModel.register(email, password) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFA84E4E)
            )
        ) {
            Text("Create Account")
        }

        TextButton(onClick = onBackToLogin) {
            Text("Back to Login")
        }

        Spacer(Modifier.height(20.dp))

        when (val s = state) {
            is AuthState.Loading -> CircularProgressIndicator()
            is AuthState.Error -> Text(s.message, color = MaterialTheme.colorScheme.error)
            else -> {}
        }
    }
}
