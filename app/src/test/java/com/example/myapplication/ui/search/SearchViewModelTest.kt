package com.example.myapplication.ui.search

import com.example.myapplication.data.remote.GifDto
import com.example.myapplication.data.remote.GifImageDto
import com.example.myapplication.data.remote.GiphyApi
import com.example.myapplication.data.remote.GiphyResponse
import com.example.myapplication.data.remote.ImagesDto
import com.example.myapplication.data.remote.SingleGifResponse
import com.example.myapplication.data.repository.GifRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun search_returns_gifs_and_updates_state() = runTest(testDispatcher) {
        val repository = GifRepository(FakeGiphyApi())
        val viewModel = SearchViewModel(repository)

        viewModel.onTextQueryChanged("cats")

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(2, state.gifs.size)
        assertEquals("1", state.gifs[0].id)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun loadMore_appends_next_page() = runTest(testDispatcher) {
        val repository = GifRepository(FakeGiphyApi())
        val viewModel = SearchViewModel(repository)

        viewModel.onTextQueryChanged("cats")

        advanceUntilIdle()

        viewModel.loadMore()

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals(4, state.gifs.size)
        assertEquals("3", state.gifs[2].id)
        assertEquals("4", state.gifs[3].id)
    }
}

private class FakeGiphyApi : GiphyApi {

    override suspend fun searchGifs(
        apiKey: String,
        query: String,
        limit: Int,
        offset: Int
    ): GiphyResponse {

        return if (offset == 0) {
            GiphyResponse(
                data = listOf(
                    fakeGif("1"),
                    fakeGif("2")
                )
            )
        } else {
            GiphyResponse(
                data = listOf(
                    fakeGif("3"),
                    fakeGif("4")
                )
            )
        }
    }

    override suspend fun searchGifById(
        id: String,
        apiKey: String
    ): SingleGifResponse {
        return SingleGifResponse(
            data = fakeGif(id)
        )
    }
}

private fun fakeGif(id: String): GifDto {
    return GifDto(
        id = id,
        title = "GIF $id",
        images = ImagesDto(
            original = GifImageDto(
                url = "https://example.com/$id-original.gif"
            ),
            fixedWidth = GifImageDto(
                url = "https://example.com/$id.gif"
            )
        )
    )
}
