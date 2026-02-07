package com.example.kdragonball.shared.feature.planet.domain.repository

import arrow.core.Either
import com.example.kdragonball.shared.feature.planet.domain.model.Planet
import com.example.kdragonball.shared.feature.planet.domain.model.PlanetError
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for accessing Dragon Ball planet data
 */
interface PlanetRepository {
    /**
     * Observe all planets with real-time updates
     */
    fun observePlanets(): Flow<List<Planet>>

    /**
     * Observe a specific planet by ID with real-time updates
     */
    fun observePlanet(planetId: Int): Flow<Planet?>

    /**
     * Get paginated list of planets
     * @param page Page number (starting from 1)
     * @param limit Number of items per page
     */
    suspend fun getPlanets(page: Int = 1, limit: Int = 10): Either<PlanetError, List<Planet>>

    /**
     * Get a specific planet by ID
     */
    suspend fun getPlanet(planetId: Int): Either<PlanetError, Planet>

    /**
     * Search planets by name
     */
    suspend fun searchPlanets(query: String): Either<PlanetError, List<Planet>>
}
