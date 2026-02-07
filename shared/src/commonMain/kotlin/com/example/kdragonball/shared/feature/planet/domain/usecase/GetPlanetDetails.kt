package com.example.kdragonball.shared.feature.planet.domain.usecase

import arrow.core.Either
import com.example.kdragonball.shared.feature.planet.domain.model.Planet
import com.example.kdragonball.shared.feature.planet.domain.model.PlanetError
import com.example.kdragonball.shared.feature.planet.domain.repository.PlanetRepository

/**
 * Use case for retrieving details of a specific Dragon Ball planet
 */
class GetPlanetDetails(
    private val repository: PlanetRepository
) {
    suspend operator fun invoke(planetId: Int): Either<PlanetError, Planet> {
        return repository.getPlanet(planetId)
    }
}
