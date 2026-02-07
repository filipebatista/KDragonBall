package com.example.kdragonball.shared.feature.character.di

import com.example.kdragonball.shared.core.network.DragonBallApiDataSource
import com.example.kdragonball.shared.feature.character.data.datasource.CharacterDataSource
import com.example.kdragonball.shared.feature.character.data.repository.CharacterRepositoryImpl
import com.example.kdragonball.shared.feature.character.domain.repository.CharacterRepository
import com.example.kdragonball.shared.feature.character.domain.usecase.GetCharacterDetails
import com.example.kdragonball.shared.feature.character.domain.usecase.GetCharacters
import com.example.kdragonball.shared.feature.character.domain.usecase.SearchCharacters
import com.example.kdragonball.shared.feature.character.presentation.viewmodel.CharacterDetailViewModel
import com.example.kdragonball.shared.feature.character.presentation.viewmodel.CharacterListViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val characterModule =
    module {
        // Data sources
        single<CharacterDataSource> { get<DragonBallApiDataSource>() }

        // Repositories
        singleOf(::CharacterRepositoryImpl) bind CharacterRepository::class

        // Use cases
        factoryOf(::GetCharacters)
        factoryOf(::GetCharacterDetails)
        factoryOf(::SearchCharacters)

        // ViewModels
        viewModelOf(::CharacterListViewModel)
        viewModel { params ->
            CharacterDetailViewModel(
                getCharacterDetails = get(),
                characterId = params.get()
            )
        }
    }
