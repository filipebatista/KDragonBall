package com.example.kdragonball.shared.feature.planet.domain.usecase

import arrow.core.right
import com.example.kdragonball.shared.core.testutil.TestBuilders
import com.example.kdragonball.shared.feature.planet.domain.FakePlanetRepository
import com.example.kdragonball.shared.feature.planet.domain.model.PlanetError
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class GetPlanetDetailsTest {
    private lateinit var repository: FakePlanetRepository
    private lateinit var getPlanetDetails: GetPlanetDetails

    @BeforeTest
    fun setUp() {
        repository = FakePlanetRepository()
        getPlanetDetails = GetPlanetDetails(repository)
    }

    @Test
    fun `given planet exists when getting planet details then returns success with planet`() =
        runTest {
            // Given
            val planet = TestBuilders.planet(id = 1, name = "Earth")
            repository.setPlanets(listOf(planet))

            // When
            val result = getPlanetDetails(planetId = 1)

            // Then
            assertTrue(result.isRight())
            result.onRight { data ->
                assertEquals("Earth", data.name)
            }
        }

    @Test
    fun `given planet does not exist when getting planet details then returns not found error`() =
        runTest {
            // Given
            repository.setPlanets(emptyList())

            // When
            val result = getPlanetDetails(planetId = 999)

            // Then
            assertTrue(result.isLeft())
            result.onLeft { error ->
                assertTrue(error is PlanetError.NotFound)
            }
        }

    @Test
    fun `given repository fails when getting planet details then returns error`() = runTest {
        // Given
        repository.shouldFailWithError = PlanetError.NetworkError("Connection failed")

        // When
        val result = getPlanetDetails(planetId = 1)

        // Then
        assertTrue(result.isLeft())
        result.onLeft { error ->
            assertTrue(error is PlanetError.NetworkError)
        }
    }

    @Test
    fun `given planet id when getting planet details then passes correct id to repository`() =
        runTest {
            // Given
            val planet = TestBuilders.planet(id = 42)
            repository.setPlanets(listOf(planet))

            // When
            getPlanetDetails(planetId = 42)

            // Then
            assertEquals(1, repository.getPlanetCalls.size)
            assertEquals(42, repository.getPlanetCalls.first())
        }

    @Test
    fun `given destroyed planet when getting planet details then returns planet with destroyed status`() =
        runTest {
            // Given
            val planet = TestBuilders.planet(id = 1, name = "Vegeta", isDestroyed = true)
            repository.setPlanets(listOf(planet))

            // When
            val result = getPlanetDetails(planetId = 1)

            // Then
            assertTrue(result.isRight())
            result.onRight { returnedPlanet ->
                assertEquals("Vegeta", returnedPlanet.name)
                assertTrue(returnedPlanet.isDestroyed)
            }
        }

    @Test
    fun `given repository configured to return specific planet when getting details then returns that planet`() =
        runTest {
            // Given
            val customPlanet = TestBuilders.planet(id = 99, name = "Namek")
            repository.getPlanetResult = customPlanet.right()

            // When
            val result = getPlanetDetails(planetId = 1)

            // Then
            assertTrue(result.isRight())
            result.onRight { data ->
                assertEquals("Namek", data.name)
            }
        }
}
