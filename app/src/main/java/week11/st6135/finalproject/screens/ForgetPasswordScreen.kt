package week11.st6135.finalproject.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import week11.st6135.finalproject.util.AuthState
import week11.st6135.finalproject.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onBackToLogin: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Reset Password", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter your email") }
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { viewModel.sendPasswordReset(email) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Send Reset Email") }

        TextButton(onClick = onBackToLogin) {
            Text("Back to Login")
        }

        Spacer(Modifier.height(20.dp))

        when (val s = state) {
            is AuthState.Loading -> CircularProgressIndicator()
            is AuthState.PasswordResetSent ->
                Text("Reset email sent to ${s.email}", color = MaterialTheme.colorScheme.primary)
            is AuthState.Error ->
                Text(s.message, color = MaterialTheme.colorScheme.error)
            else -> {}
        }
    }
}
