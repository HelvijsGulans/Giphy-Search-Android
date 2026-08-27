package com.example.myapplication.ui.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
fun DetailsScreen(
    gifId: String,
    viewModel: DetailsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(gifId) {
        viewModel.loadGif(gifId)
    }

    when {
        uiState.isLoading -> {
            Text("Loading GIF $gifId...")
        }

        uiState.error != null -> {
            Text("Error: ${uiState.error}")
        }

        uiState.gif != null -> {
            val gif = uiState.gif!!

            Column {
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

        else -> {
            Text("No GIF loaded")
        }
    }
}