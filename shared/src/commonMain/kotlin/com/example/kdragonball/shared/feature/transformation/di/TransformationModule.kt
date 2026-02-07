package com.example.kdragonball.shared.feature.transformation.di

import com.example.kdragonball.shared.core.network.DragonBallApiDataSource
import com.example.kdragonball.shared.feature.transformation.data.datasource.TransformationDataSource
import com.example.kdragonball.shared.feature.transformation.data.repository.TransformationRepositoryImpl
import com.example.kdragonball.shared.feature.transformation.domain.repository.TransformationRepository
import com.example.kdragonball.shared.feature.transformation.domain.usecase.GetTransformationDetails
import com.example.kdragonball.shared.feature.transformation.domain.usecase.GetTransformations
import com.example.kdragonball.shared.feature.transformation.domain.usecase.SearchTransformations
import com.example.kdragonball.shared.feature.transformation.presentation.viewmodel.TransformationDetailViewModel
import com.example.kdragonball.shared.feature.transformation.presentation.viewmodel.TransformationListViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val transformationModule =
    module {
        // Data sources
        single<TransformationDataSource> { get<DragonBallApiDataSource>() }

        // Repositories
        singleOf(::TransformationRepositoryImpl) bind TransformationRepository::class

        // Use cases
        factoryOf(::GetTransformations)
        factoryOf(::GetTransformationDetails)
        factoryOf(::SearchTransformations)

        // ViewModels
        viewModelOf(::TransformationListViewModel)
        viewModel { params ->
            TransformationDetailViewModel(
                getTransformationDetails = get(),
                transformationId = params.get()
            )
        }
    }
