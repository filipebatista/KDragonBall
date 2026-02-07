package com.example.kdragonball.shared.feature.planet.domain.usecase

import arrow.core.Either
import com.example.kdragonball.shared.feature.planet.domain.model.Planet
import com.example.kdragonball.shared.feature.planet.domain.model.PlanetError
import com.example.kdragonball.shared.feature.planet.domain.repository.PlanetRepository

/**
 * Use case for searching Dragon Ball planets by name
 */
class SearchPlanets(
    private val repository: PlanetRepository
) {
    suspend operator fun invoke(query: String): Either<PlanetError, List<Planet>> {
        return repository.searchPlanets(query)
    }
}
