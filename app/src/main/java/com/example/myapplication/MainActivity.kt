package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.myapplication.ui.details.AppNavigation
import com.example.myapplication.ui.details.DetailsViewModel
import com.example.myapplication.ui.search.SearchUiState
import com.example.myapplication.ui.search.SearchViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {

    private val searchViewModel: SearchViewModel by viewModel()
    private val detailsViewModel: DetailsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                AppNavigation(
                    searchViewModel = searchViewModel,
                    detailsViewModel = detailsViewModel
                )
            }
        }
    }
}



@Composable
fun SearchQuery(
    viewModel: SearchViewModel,
    onGifClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    SearchContent(
        uiState = uiState,
        onQueryChanged = viewModel::onTextQueryChanged,
        onGifClick = onGifClick,
        onLoadMore = viewModel::loadMore
    )
}

@Composable
fun SearchContent(
    uiState: SearchUiState,
    onQueryChanged: (String) -> Unit,
    onGifClick: (String) -> Unit,
    onLoadMore: () -> Unit
) {
    val gridState = rememberLazyGridState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(24.dp)
    ) {

        OutlinedTextField(
            value = uiState.query,
            onValueChange = onQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Search GIFs")
            },
            placeholder = {
                Text("Search...")
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        if (
            uiState.query.isBlank() &&
            uiState.gifs.isEmpty()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Search for your favorite GIFs",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp
                )
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp)
            )
        }

        uiState.error?.let {
            Text(it)
        }

        if (
            !uiState.isLoading &&
            uiState.error == null &&
            uiState.query.isNotBlank() &&
            uiState.gifs.isEmpty()
        ) {
            Text(
                text = "No GIFs found",
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        LaunchedEffect(gridState) {
            snapshotFlow {
                val lastVisibleIndex =
                    gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

                val totalItems =
                    gridState.layoutInfo.totalItemsCount

                totalItems > 0 &&
                        lastVisibleIndex >= totalItems - 5
            }
                .distinctUntilChanged()
                .collect { shouldLoadMore ->
                    if (shouldLoadMore) {
                        onLoadMore()
                    }
                }
        }

        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Adaptive(minSize = 140.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(uiState.gifs) { gif ->
                AsyncImage(
                    model = gif.images.fixedWidth.url,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            onGifClick(gif.id)
                        },
                    contentDescription = gif.title,
                    contentScale = ContentScale.Crop
                )
            }
        }

        if (uiState.isLoadingMore) {
            CircularProgressIndicator(
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchContentPreview() {
    MyApplicationTheme {
        SearchContent(
            uiState = SearchUiState(
                query = "cats"
            ),
            onQueryChanged = {},
            onGifClick = {},
            onLoadMore = {}
        )
    }
}






