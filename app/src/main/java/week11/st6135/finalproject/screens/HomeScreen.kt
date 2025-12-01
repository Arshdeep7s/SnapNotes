package week11.st6135.finalproject.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import week11.st6135.finalproject.R


@Composable
fun HomeScreen(
    onViewNotes: () -> Unit,
    onAddNote: () -> Unit,
    onLogout: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                "SnapNotes",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 38.sp,
                    color = Color(0xFFA84E4E)
                )
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Capture. Extract. Save.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 20.sp,
                    color = Color(0xFF90CAF9)
                )
            )

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = onAddNote,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add New Note")
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onViewNotes,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View All Notes")
            }

            Spacer(Modifier.height(32.dp))

            // ---- NEW LOGOUT BUTTON ----
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.width(150.dp)
            ) {
                Text(text = "Logout", color = Color(0xFFFFFFFF))
            }
        }
    }
}