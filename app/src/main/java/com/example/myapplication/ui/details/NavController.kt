package com.example.myapplication.ui.details

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.SearchQuery
import com.example.myapplication.ui.search.SearchViewModel

@Composable
fun AppNavigation(
    searchViewModel: SearchViewModel,
    detailsViewModel: DetailsViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "search"
    ) {
        composable("search") {
            SearchQuery(
                viewModel = searchViewModel,
                onGifClick = { gifId ->
                    navController.navigate("details/$gifId")
                }
            )
        }
        composable("details/{gifId}") { backStackEntry ->
            val gifId = backStackEntry.arguments?.getString("gifId") ?: ""

            DetailsScreen(
                gifId = gifId,
                viewModel = detailsViewModel
            )
        }
    }
}
