package com.example.kdragonball.shared.feature.planet.domain.usecase

import arrow.core.Either
import com.example.kdragonball.shared.feature.planet.domain.model.Planet
import com.example.kdragonball.shared.feature.planet.domain.model.PlanetError
import com.example.kdragonball.shared.feature.planet.domain.repository.PlanetRepository

/**
 * Use case for retrieving a list of Dragon Ball planets
 */
class GetPlanets(
    private val repository: PlanetRepository
) {
    suspend operator fun invoke(page: Int = 1, limit: Int = 10): Either<PlanetError, List<Planet>> {
        return repository.getPlanets(page, limit)
    }
}
