package com.example.kdragonball.shared.feature.planet.di

import com.example.kdragonball.shared.core.network.DragonBallApiDataSource
import com.example.kdragonball.shared.feature.planet.data.datasource.PlanetDataSource
import com.example.kdragonball.shared.feature.planet.data.repository.PlanetRepositoryImpl
import com.example.kdragonball.shared.feature.planet.domain.repository.PlanetRepository
import com.example.kdragonball.shared.feature.planet.domain.usecase.GetPlanetDetails
import com.example.kdragonball.shared.feature.planet.domain.usecase.GetPlanets
import com.example.kdragonball.shared.feature.planet.domain.usecase.SearchPlanets
import com.example.kdragonball.shared.feature.planet.presentation.viewmodel.PlanetDetailViewModel
import com.example.kdragonball.shared.feature.planet.presentation.viewmodel.PlanetListViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val planetModule =
    module {
        // Data sources
        single<PlanetDataSource> { get<DragonBallApiDataSource>() }

        // Repositories
        singleOf(::PlanetRepositoryImpl) bind PlanetRepository::class

        // Use cases
        factoryOf(::GetPlanets)
        factoryOf(::GetPlanetDetails)
        factoryOf(::SearchPlanets)

        // ViewModels
        viewModelOf(::PlanetListViewModel)
        viewModel { params ->
            PlanetDetailViewModel(
                getPlanetDetails = get(),
                planetId = params.get()
            )
        }
    }
