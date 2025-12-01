package week11.st6135.finalproject.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import week11.st6135.finalproject.viewmodel.NotesViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    userId: String,
    notesViewModel: NotesViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var extractedText by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraAvailable = deviceHasCamera(context)

    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }



    val cameraImageUri = remember {
        val file = File(context.cacheDir, "temp_${System.currentTimeMillis()}.jpg")
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    // ---------------- CAMERA BUTTON WITH ERROR HANDLING ----------------
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUri = cameraImageUri
            isProcessing = true
            processImage(cameraImageUri, context) {
                extractedText = it
                isProcessing = false
            }
        } else {
            snackbarMessage = "Failed to capture image."
        }
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    val cameraPermission = Manifest.permission.CAMERA
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(cameraImageUri)
        } else {
            snackbarMessage = "Camera permission is required."
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            tempImageUri = it
            isProcessing = true
            processImage(it, context) {
                extractedText = it
                isProcessing = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Note", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF43173E)
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Heading Match With Login/Register
            Text(
                "Create a New Note",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 30.sp,
                    color = Color(0xFFA84E4E)
                ),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Camera / Gallery Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, cameraPermission)
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            permissionLauncher.launch(cameraPermission)
                        } else {
                            cameraLauncher.launch(cameraImageUri)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CameraAlt, null)
                    Spacer(Modifier.width(4.dp))
                    Text("Camera")
                }



                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Photo, null)
                    Spacer(Modifier.width(4.dp))
                    Text("Gallery")
                }
            }

            Spacer(Modifier.height(20.dp))

            // Processing Indicator
            if (isProcessing) {
                CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
                Text("Extracting text...")
                Spacer(Modifier.height(20.dp))
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = extractedText,
                onValueChange = { extractedText = it },
                label = { Text("Extracted Text") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                maxLines = 10
            )

            Spacer(Modifier.height(30.dp))

            Button(
                onClick = {
                    notesViewModel.addNote(
                        userId = userId,
                        title = title,
                        text = extractedText
                    ) {
                        onBack()
                    }
                },
                enabled = extractedText.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Note")
            }
        }
    }
}


private fun processImage(uri: Uri, context: android.content.Context, onTextExtracted: (String) -> Unit) {
    try {
        val image = InputImage.fromFilePath(context, uri)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                onTextExtracted(visionText.text)
            }
            .addOnFailureListener { e ->
                onTextExtracted("Error extracting text: ${e.message}")
            }
    } catch (e: Exception) {
        onTextExtracted("Error loading image: ${e.message}")
    }
}

fun deviceHasCamera(context: android.content.Context): Boolean {
    val pm = context.packageManager
    return pm.hasSystemFeature(android.content.pm.PackageManager.FEATURE_CAMERA_ANY)
}
