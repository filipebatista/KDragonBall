package com.example.kdragonball.shared.feature.planet.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.example.kdragonball.shared.feature.planet.data.datasource.PlanetDataSource
import com.example.kdragonball.shared.feature.planet.domain.model.Planet
import com.example.kdragonball.shared.feature.planet.domain.model.PlanetError
import com.example.kdragonball.shared.feature.planet.domain.repository.PlanetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Implementation of PlanetRepository using the API data source
 */
class PlanetRepositoryImpl(
    private val dataSource: PlanetDataSource
) : PlanetRepository {
    // Cache for planets
    private val planetsCache = MutableStateFlow<Map<Int, Planet>>(emptyMap())

    override fun observePlanets(): Flow<List<Planet>> {
        return planetsCache.map { it.values.toList() }
    }

    override fun observePlanet(planetId: Int): Flow<Planet?> {
        return planetsCache.map { it[planetId] }
    }

    override suspend fun getPlanets(page: Int, limit: Int): Either<PlanetError, List<Planet>> {
        return try {
            val response = dataSource.fetchPlanets(page, limit)
            val planets = response.items

            // Update cache
            planetsCache.value = planetsCache.value + planets.associateBy { it.id }

            planets.right()
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun getPlanet(planetId: Int): Either<PlanetError, Planet> {
        return try {
            // Check cache first
            planetsCache.value[planetId]?.let {
                return it.right()
            }

            // Fetch from API
            val planet = dataSource.fetchPlanet(planetId)

            // Update cache
            planetsCache.value = planetsCache.value + (planet.id to planet)

            planet.right()
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override suspend fun searchPlanets(query: String): Either<PlanetError, List<Planet>> {
        return try {
            if (query.isBlank()) {
                return planetsCache.value.values.toList().right()
            }

            val planets = dataSource.searchPlanets(query)

            // Update cache
            planetsCache.value = planetsCache.value + planets.associateBy { it.id }

            planets.right()
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun <T> handleException(e: Exception): Either<PlanetError, T> {
        return when {
            e.message?.contains("404") == true -> {
                PlanetError.NotFound("Planet not found").left()
            }
            e.message?.contains("network") == true || e.message?.contains("timeout") == true -> {
                PlanetError.NetworkError("Network error: ${e.message}").left()
            }
            else -> {
                PlanetError.UnknownError("Unknown error: ${e.message}").left()
            }
        }
    }
}
