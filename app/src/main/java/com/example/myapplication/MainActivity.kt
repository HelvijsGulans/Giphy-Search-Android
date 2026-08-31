package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.TextField
import androidx.compose.runtime.*

import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.search.SearchViewModel

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.example.myapplication.ui.details.AppNavigation
import com.example.myapplication.ui.details.DetailsViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.search.SearchUiState
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
            .safeDrawingPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TextField(
            value = uiState.query,
            onValueChange = onQueryChanged,
            label = {
                Text("Search GIFs")
            }
        )

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
            Text("No GIFs found")
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
                .fillMaxWidth()
        ) {
            items(uiState.gifs) { gif ->
                AsyncImage(
                    model = gif.images.fixedWidth.url,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
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






