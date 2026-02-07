package com.example.kdragonball.shared.feature.planet.data.datasource

import com.example.kdragonball.shared.core.model.PaginatedResponse
import com.example.kdragonball.shared.feature.planet.domain.model.Planet

/**
 * Data source interface for fetching planet data from the API
 */
interface PlanetDataSource {
    /**
     * Fetch paginated list of planets from the API
     */
    suspend fun fetchPlanets(page: Int, limit: Int): PaginatedResponse<Planet>

    /**
     * Fetch a specific planet by ID
     */
    suspend fun fetchPlanet(planetId: Int): Planet

    /**
     * Search planets by name
     */
    suspend fun searchPlanets(query: String): List<Planet>
}
