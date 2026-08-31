package com.example.myapplication.di

import com.example.myapplication.data.remote.RetrofitInstance
import com.example.myapplication.data.repository.GifRepository
import com.example.myapplication.ui.details.DetailsViewModel
import com.example.myapplication.ui.search.SearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    single {
        RetrofitInstance.api
    }

    single {
        GifRepository(get())
    }

    viewModelOf(::SearchViewModel)
    viewModelOf(::DetailsViewModel)
}

