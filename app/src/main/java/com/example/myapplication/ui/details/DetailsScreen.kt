package com.example.myapplication.ui.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun DetailsScreen(
    gifId: String,
    viewModel: DetailsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(gifId) {
        viewModel.loadGif(gifId)
    }

    DetailsContent(
        uiState = uiState,
        onBack = onBack
    )
}

@Composable
fun DetailsContent(
    uiState: DetailsUiState,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp)
    ) {
        Button(
            onClick = onBack,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text("Back")
        }

        when {
            uiState.isLoading -> {
                Text("Loading...")
            }

            uiState.error != null -> {
                Text("Error: ${uiState.error}")
            }

            uiState.gif != null -> {
                val gif = uiState.gif

                Text(gif.title)

                AsyncImage(
                    model = gif.images.fixedWidth.url,
                    contentDescription = gif.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsContentPreview() {
    MyApplicationTheme {
        DetailsContent(
            uiState = DetailsUiState(
                isLoading = true
            ),
            onBack = {}
        )
    }
}