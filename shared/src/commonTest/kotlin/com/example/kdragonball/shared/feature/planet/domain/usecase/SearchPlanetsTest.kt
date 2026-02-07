package com.example.kdragonball.shared.feature.planet.domain.usecase

import arrow.core.getOrElse
import arrow.core.right
import com.example.kdragonball.shared.core.testutil.TestBuilders
import com.example.kdragonball.shared.feature.planet.domain.FakePlanetRepository
import com.example.kdragonball.shared.feature.planet.domain.model.PlanetError
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class SearchPlanetsTest {
    private lateinit var repository: FakePlanetRepository
    private lateinit var searchPlanets: SearchPlanets

    @BeforeTest
    fun setUp() {
        repository = FakePlanetRepository()
        searchPlanets = SearchPlanets(repository)
    }

    @Test
    fun `given planets exist when searching with matching query then returns matching planets`() =
        runTest {
            // Given
            val planets =
                listOf(
                    TestBuilders.planet(id = 1, name = "Earth"),
                    TestBuilders.planet(id = 2, name = "Namek"),
                    TestBuilders.planet(id = 3, name = "Vegeta")
                )
            repository.setPlanets(planets)

            // When
            val result = searchPlanets(query = "ea")

            // Then
            assertTrue(result.isRight())
            val foundPlanets = result.getOrElse { emptyList() }
            assertEquals(1, foundPlanets.size)
            assertTrue(foundPlanets.any { it.name == "Earth" })
        }

    @Test
    fun `given planets exist when searching with no matching query then returns empty list`() =
        runTest {
            // Given
            val planets =
                listOf(
                    TestBuilders.planet(id = 1, name = "Earth"),
                    TestBuilders.planet(id = 2, name = "Namek")
                )
            repository.setPlanets(planets)

            // When
            val result = searchPlanets(query = "Mars")

            // Then
            assertTrue(result.isRight())
            assertEquals(0, result.getOrElse { emptyList() }.size)
        }

    @Test
    fun `given planets exist when searching with case insensitive query then returns matching planets`() =
        runTest {
            // Given
            val planets =
                listOf(
                    TestBuilders.planet(id = 1, name = "EARTH"),
                    TestBuilders.planet(id = 2, name = "namek")
                )
            repository.setPlanets(planets)

            // When
            val result = searchPlanets(query = "earth")

            // Then
            assertTrue(result.isRight())
            val data = result.getOrElse { emptyList() }
            assertEquals(1, data.size)
            assertEquals("EARTH", data.first().name)
        }

    @Test
    fun `given repository configured to return specific result when searching then returns that result`() =
        runTest {
            // Given
            val expectedResults = listOf(TestBuilders.planet(id = 99, name = "Custom Planet"))
            repository.searchPlanetsResult = expectedResults.right()

            // When
            val result = searchPlanets(query = "anything")

            // Then
            assertTrue(result.isRight())
            assertEquals("Custom Planet", result.getOrElse { emptyList() }.first().name)
        }

    @Test
    fun `given repository fails when searching then returns error`() = runTest {
        // Given
        repository.shouldFailWithError = PlanetError.NetworkError("Search failed")

        // When
        val result = searchPlanets(query = "Earth")

        // Then
        assertTrue(result.isLeft())
        result.onLeft { error ->
            assertTrue(error is PlanetError.NetworkError)
        }
    }

    @Test
    fun `given empty query when searching then passes empty query to repository`() = runTest {
        // Given
        repository.setPlanets(TestBuilders.planetList(count = 3))

        // When
        searchPlanets(query = "")

        // Then
        assertEquals(1, repository.searchPlanetsCalls.size)
        assertEquals("", repository.searchPlanetsCalls.first())
    }

    @Test
    fun `given search query when searching then passes exact query to repository`() = runTest {
        // Given
        repository.setPlanets(emptyList())

        // When
        searchPlanets(query = "Planet Vegeta")

        // Then
        assertEquals(1, repository.searchPlanetsCalls.size)
        assertEquals("Planet Vegeta", repository.searchPlanetsCalls.first())
    }
}
