package com.example.myapplication.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
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
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        TextButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.Start)
                .heightIn(min = 48.dp)
        ) {
            Text(
                text = "<- Back",
                style = MaterialTheme.typography.titleMedium
            )
        }

        when {
            uiState.isLoading -> {
                Text(
                    text = "Loading..",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            uiState.error != null -> {
                Text(
                    text = "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            uiState.gif != null -> {
                val gif = uiState.gif

                Text(
                    text = "Name: ${gif.title}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                AsyncImage(
                    model = gif.images.fixedWidth.url,
                    contentDescription = gif.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
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