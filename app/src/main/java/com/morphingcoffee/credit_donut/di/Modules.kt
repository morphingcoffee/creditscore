package com.morphingcoffee.credit_donut.di

import com.morphingcoffee.credit_donut.BuildConfig
import com.morphingcoffee.credit_donut.score_display.model.ICreditDataApiService
import com.morphingcoffee.credit_donut.score_display.repository.ICreditDataRepository
import com.morphingcoffee.credit_donut.score_display.repository.impl.CreditDataRepository
import com.morphingcoffee.credit_donut.score_display.view.CreditScoreViewModel
import com.squareup.moshi.Moshi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

fun viewModule() = module {
    viewModel {
        CreditScoreViewModel(get())
    }
}

fun repositoryModule() = module {
    factory<ICreditDataRepository> { CreditDataRepository(get()) }
}

fun networkingModule() = module {
    factory<Moshi> {
        Moshi.Builder()
            .build()
    }
    factory<MoshiConverterFactory> {
        MoshiConverterFactory.create(get())
    }
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(get<MoshiConverterFactory>())
            .build()
    }
    // HTTP Services
    factory<ICreditDataApiService> { get<Retrofit>().create(ICreditDataApiService::class.java) }
}

fun allModules(): List<Module> = viewModule() + repositoryModule() + networkingModule()