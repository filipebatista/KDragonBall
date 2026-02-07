package com.example.kdragonball.shared.feature.planet.domain.usecase

import arrow.core.getOrElse
import com.example.kdragonball.shared.core.testutil.TestBuilders
import com.example.kdragonball.shared.feature.planet.domain.FakePlanetRepository
import com.example.kdragonball.shared.feature.planet.domain.model.PlanetError
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class GetPlanetsTest {
    private lateinit var repository: FakePlanetRepository
    private lateinit var getPlanets: GetPlanets

    @BeforeTest
    fun setUp() {
        repository = FakePlanetRepository()
        getPlanets = GetPlanets(repository)
    }

    @Test
    fun `given planets exist when getting planets then returns success with planet list`() =
        runTest {
            // Given
            val planets = TestBuilders.planetList(count = 5)
            repository.setPlanets(planets)

            // When
            val result = getPlanets(page = 1, limit = 10)

            // Then
            assertTrue(result.isRight())
            assertEquals(5, result.getOrElse { emptyList() }.size)
        }

    @Test
    fun `given no planets exist when getting planets then returns empty list`() = runTest {
        // Given
        repository.setPlanets(emptyList())

        // When
        val result = getPlanets(page = 1, limit = 10)

        // Then
        assertTrue(result.isRight())
        assertEquals(0, result.getOrElse { emptyList() }.size)
    }

    @Test
    fun `given repository fails when getting planets then returns error`() = runTest {
        // Given
        repository.shouldFailWithError = PlanetError.NetworkError("Connection failed")

        // When
        val result = getPlanets(page = 1, limit = 10)

        // Then
        assertTrue(result.isLeft())
        result.onLeft { error ->
            assertTrue(error is PlanetError.NetworkError)
        }
    }

    @Test
    fun `given planets exist when getting first page then returns correct planets`() = runTest {
        // Given
        val planets = TestBuilders.planetList(count = 15)
        repository.setPlanets(planets)

        // When
        val result = getPlanets(page = 1, limit = 10)

        // Then
        assertTrue(result.isRight())
        assertEquals(10, result.getOrElse { emptyList() }.size)
    }

    @Test
    fun `given planets exist when getting second page then returns remaining planets`() = runTest {
        // Given
        val planets = TestBuilders.planetList(count = 15)
        repository.setPlanets(planets)

        // When
        val result = getPlanets(page = 2, limit = 10)

        // Then
        assertTrue(result.isRight())
        assertEquals(5, result.getOrElse { emptyList() }.size)
    }

    @Test
    fun `given page beyond available data when getting planets then returns empty list`() =
        runTest {
            // Given
            val planets = TestBuilders.planetList(count = 5)
            repository.setPlanets(planets)

            // When
            val result = getPlanets(page = 10, limit = 10)

            // Then
            assertTrue(result.isRight())
            assertEquals(0, result.getOrElse { emptyList() }.size)
        }

    @Test
    fun `given default parameters when getting planets then uses page 1 and limit 10`() = runTest {
        // Given
        repository.setPlanets(emptyList())

        // When
        getPlanets()

        // Then
        assertEquals(1, repository.getPlanetsCalls.size)
        assertEquals(1 to 10, repository.getPlanetsCalls.first())
    }
}
