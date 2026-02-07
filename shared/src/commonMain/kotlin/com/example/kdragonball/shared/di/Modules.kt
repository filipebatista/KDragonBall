package com.example.kdragonball.shared.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.kdragonball.shared.core.di.coreModule
import com.example.kdragonball.shared.feature.character.di.characterModule
import com.example.kdragonball.shared.feature.character.domain.usecase.GetCharacterDetails
import com.example.kdragonball.shared.feature.character.domain.usecase.GetCharacters
import com.example.kdragonball.shared.feature.character.domain.usecase.SearchCharacters
import com.example.kdragonball.shared.feature.character.presentation.viewmodel.CharacterDetailViewModel
import com.example.kdragonball.shared.feature.character.presentation.viewmodel.CharacterListViewModel
import com.example.kdragonball.shared.feature.planet.di.planetModule
import com.example.kdragonball.shared.feature.planet.domain.usecase.GetPlanetDetails
import com.example.kdragonball.shared.feature.planet.domain.usecase.GetPlanets
import com.example.kdragonball.shared.feature.planet.domain.usecase.SearchPlanets
import com.example.kdragonball.shared.feature.planet.presentation.viewmodel.PlanetDetailViewModel
import com.example.kdragonball.shared.feature.planet.presentation.viewmodel.PlanetListViewModel
import com.example.kdragonball.shared.feature.transformation.di.transformationModule
import com.example.kdragonball.shared.feature.transformation.domain.usecase.GetTransformationDetails
import com.example.kdragonball.shared.feature.transformation.domain.usecase.GetTransformations
import com.example.kdragonball.shared.feature.transformation.domain.usecase.SearchTransformations
import com.example.kdragonball.shared.feature.transformation.presentation.viewmodel.TransformationDetailViewModel
import com.example.kdragonball.shared.feature.transformation.presentation.viewmodel.TransformationListViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

val sharedModules =
    listOf(
        coreModule,
        characterModule,
        planetModule,
        transformationModule
    )

// ViewModelFactory for iOS using Koin
object ViewModelFactories : KoinComponent {
    // Character ViewModels
    val characterListViewModelFactory: ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                val getCharacters: GetCharacters by inject()
                val searchCharacters: SearchCharacters by inject()
                CharacterListViewModel(getCharacters, searchCharacters)
            }
        }

    fun characterDetailViewModelFactory(characterId: Int): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                val getCharacterDetails: GetCharacterDetails by inject()
                CharacterDetailViewModel(getCharacterDetails, characterId)
            }
        }

    // Planet ViewModels
    val planetListViewModelFactory: ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                val getPlanets: GetPlanets by inject()
                val searchPlanets: SearchPlanets by inject()
                PlanetListViewModel(getPlanets, searchPlanets)
            }
        }

    fun planetDetailViewModelFactory(planetId: Int): ViewModelProvider.Factory = viewModelFactory {
        initializer {
            val getPlanetDetails: GetPlanetDetails by inject()
            PlanetDetailViewModel(getPlanetDetails, planetId)
        }
    }

    // Transformation ViewModels
    val transformationListViewModelFactory: ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                val getTransformations: GetTransformations by inject()
                val searchTransformations: SearchTransformations by inject()
                TransformationListViewModel(getTransformations, searchTransformations)
            }
        }

    fun transformationDetailViewModelFactory(transformationId: Int): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                val getTransformationDetails: GetTransformationDetails by inject()
                TransformationDetailViewModel(getTransformationDetails, transformationId)
            }
        }
}
