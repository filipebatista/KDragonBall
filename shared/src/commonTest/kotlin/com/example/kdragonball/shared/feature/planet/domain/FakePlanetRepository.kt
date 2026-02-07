package com.example.kdragonball.shared.feature.planet.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.example.kdragonball.shared.feature.planet.domain.model.Planet
import com.example.kdragonball.shared.feature.planet.domain.model.PlanetError
import com.example.kdragonball.shared.feature.planet.domain.repository.PlanetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Fake implementation of PlanetRepository for testing.
 * Allows configuring responses and tracking method calls.
 */
class FakePlanetRepository : PlanetRepository {
    private val planetsStore = MutableStateFlow<Map<Int, Planet>>(emptyMap())

    // Configuration for controlling behavior
    var getPlanetsResult: Either<PlanetError, List<Planet>>? = null
    var getPlanetResult: Either<PlanetError, Planet>? = null
    var searchPlanetsResult: Either<PlanetError, List<Planet>>? = null

    // Error simulation
    var shouldFailWithError: PlanetError? = null

    // Call tracking
    var getPlanetsCalls = mutableListOf<Pair<Int, Int>>() // page, limit
    var getPlanetCalls = mutableListOf<Int>() // planetId
    var searchPlanetsCalls = mutableListOf<String>() // query

    /**
     * Prepopulate the store with planets for testing
     */
    fun setPlanets(planets: List<Planet>) {
        planetsStore.value = planets.associateBy { it.id }
    }

    fun clearPlanets() {
        planetsStore.value = emptyMap()
    }

    fun reset() {
        planetsStore.value = emptyMap()
        getPlanetsResult = null
        getPlanetResult = null
        searchPlanetsResult = null
        shouldFailWithError = null
        getPlanetsCalls.clear()
        getPlanetCalls.clear()
        searchPlanetsCalls.clear()
    }

    override fun observePlanets(): Flow<List<Planet>> {
        return planetsStore.map { it.values.toList() }
    }

    override fun observePlanet(planetId: Int): Flow<Planet?> {
        return planetsStore.map { it[planetId] }
    }

    override suspend fun getPlanets(page: Int, limit: Int): Either<PlanetError, List<Planet>> {
        getPlanetsCalls.add(page to limit)

        shouldFailWithError?.let { error ->
            return error.left()
        }

        getPlanetsResult?.let { return it }

        // Default behavior: return planets from store with pagination
        val allPlanets = planetsStore.value.values.toList()
        val startIndex = (page - 1) * limit
        val endIndex = minOf(startIndex + limit, allPlanets.size)

        return if (startIndex < allPlanets.size) {
            allPlanets.subList(startIndex, endIndex).right()
        } else {
            emptyList<Planet>().right()
        }
    }

    override suspend fun getPlanet(planetId: Int): Either<PlanetError, Planet> {
        getPlanetCalls.add(planetId)

        shouldFailWithError?.let { error ->
            return error.left()
        }

        getPlanetResult?.let { return it }

        // Default behavior: return planet from store
        val planet = planetsStore.value[planetId]
        return if (planet != null) {
            planet.right()
        } else {
            PlanetError.NotFound("Planet with id $planetId not found").left()
        }
    }

    override suspend fun searchPlanets(query: String): Either<PlanetError, List<Planet>> {
        searchPlanetsCalls.add(query)

        shouldFailWithError?.let { error ->
            return error.left()
        }

        searchPlanetsResult?.let { return it }

        // Default behavior: filter planets by name
        val filteredPlanets =
            planetsStore.value.values
                .filter { it.name.contains(query, ignoreCase = true) }
        return filteredPlanets.right()
    }
}
