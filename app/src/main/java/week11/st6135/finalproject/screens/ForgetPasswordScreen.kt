package week11.st6135.finalproject.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ---------- TITLE ----------
        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA84E4E)
            )
        )

        Spacer(Modifier.height(30.dp))

        // ---------- EMAIL FIELD ----------
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = {
                Text(
                    "Enter your email",
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

        Spacer(Modifier.height(26.dp))

        // ---------- RESET BUTTON ----------
        Button(
            onClick = { viewModel.sendPasswordReset(email) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFA84E4E)
            )
        ) {
            Text(
                "Send Reset Email",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        }

        Spacer(Modifier.height(12.dp))

        // ---------- BACK TO LOGIN ----------
        TextButton(onClick = onBackToLogin) {
            Text(
                "Back to Login",
                color = Color(0xFFA84E4E),
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.height(20.dp))

        // ---------- STATE HANDLING ----------
        when (val s = state) {
            is AuthState.Loading ->
                CircularProgressIndicator(color = Color(0xFFA84E4E))

            is AuthState.PasswordResetSent ->
                Text(
                    text = "Reset email sent to ${s.email}",
                    color = Color(0xFFA84E4E),
                    fontWeight = FontWeight.SemiBold
                )

            is AuthState.Error ->
                Text(
                    text = s.message,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )

            else -> {}
        }
    }
}
