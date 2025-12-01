package week11.st6135.finalproject.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import week11.st6135.finalproject.util.AuthState
import week11.st6135.finalproject.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onLoggedIn: () ->  Unit
) {
    val state by viewModel.state.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // ---------- TITLE ----------
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 42.sp,
                color = Color(0xFFA84E4E) // same theme as SnapNotes
            )
        )

        Spacer(Modifier.height(30.dp))

        // ---------- EMAIL FIELD ----------
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = {
                Text(
                    "Email",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFA84E4E),
                cursorColor = Color(0xFFA84E4E)
            )
        )

        Spacer(Modifier.height(16.dp))

        // ---------- PASSWORD FIELD ----------
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    "Password",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFA84E4E),
                cursorColor = Color(0xFFA84E4E)
            )
        )

        Spacer(Modifier.height(26.dp))

        // ---------- LOGIN BUTTON ----------
        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFA84E4E)
            )
        ) {
            Text(
                "Login",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(12.dp))

        // ---------- REGISTER ----------
        TextButton(onClick = onRegisterClick) {
            Text(
                "Don't have an account? Register",
                color = Color(0xFFA84E4E)
            )
        }

        // ---------- FORGOT PASSWORD ----------
        TextButton(onClick = onForgotPasswordClick) {
            Text(
                "Forgot Password?",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(20.dp))

        // ---------- AUTH STATE ----------
        when (val s = state) {
            is AuthState.Loading -> CircularProgressIndicator(color = Color(0xFFA84E4E))

            is AuthState.Success -> {
                Text("Logged in as ${s.user.email}", color = Color(0xFFA84E4E))
                onLoggedIn()
            }

            is AuthState.Error -> {
                Text(
                    text = s.message,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }
            else -> {}
        }
    }
}
