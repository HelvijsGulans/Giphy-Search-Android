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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.runtime.*

import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.remote.RetrofitInstance
import com.example.myapplication.data.repository.GifRepository
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
import kotlin.collections.get


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = GifRepository(RetrofitInstance.api)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(repository) as T
            }
        }

        val searchViewModel =
            ViewModelProvider(this, factory)[SearchViewModel::class.java]

        val detailsFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DetailsViewModel(repository) as T
            }
        }

        val detailsViewModel = ViewModelProvider(this, detailsFactory)[DetailsViewModel::class.java]

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
fun SearchQuery (viewModel: SearchViewModel, onGifClick: (String) -> Unit) {

    val uiState by viewModel.uiState.collectAsState()
    val gridState = rememberLazyGridState()


    Column (
        modifier = Modifier
        .fillMaxSize()
        .safeDrawingPadding()
        .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = uiState.query,
            onValueChange = {
                viewModel.onTextQueryChanged(it)
            },
            label = {
                Text("Search GIFs")
            }
        )

        if (uiState.isLoading) {

            Text("Loading...")
        }

        uiState.error?.let {
            Text(it)
        }

        Text("Loaded: ${uiState.gifs.size}")

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
                        viewModel.loadMore()
                    }
                }
        }

        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f).fillMaxWidth()

        ) {
            items(uiState.gifs) { gif ->
                AsyncImage(
                    model = gif.images.fixedWidth.url,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable{onGifClick(gif.id)},
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }

        }

    }
    }





