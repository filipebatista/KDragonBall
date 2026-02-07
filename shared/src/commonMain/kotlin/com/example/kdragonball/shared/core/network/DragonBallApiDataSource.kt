package com.example.kdragonball.shared.core.network

import com.example.kdragonball.shared.core.model.PaginatedResponse
import com.example.kdragonball.shared.feature.character.data.datasource.CharacterDataSource
import com.example.kdragonball.shared.feature.character.domain.model.Character
import com.example.kdragonball.shared.feature.planet.data.datasource.PlanetDataSource
import com.example.kdragonball.shared.feature.planet.domain.model.Planet
import com.example.kdragonball.shared.feature.transformation.data.datasource.TransformationDataSource
import com.example.kdragonball.shared.feature.transformation.domain.model.TransformationDetail
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * Dragon Ball API data source implementation using Ktor
 */
class DragonBallApiDataSource(
    private val httpClient: HttpClient
) : CharacterDataSource, PlanetDataSource, TransformationDataSource {
    companion object {
        private const val BASE_URL = "https://dragonball-api.com/api"
    }

    // Character endpoints
    override suspend fun fetchCharacters(page: Int, limit: Int): PaginatedResponse<Character> {
        return httpClient.get("$BASE_URL/characters") {
            parameter("page", page)
            parameter("limit", limit)
        }.body()
    }

    override suspend fun fetchCharacter(characterId: Int): Character {
        return httpClient.get("$BASE_URL/characters/$characterId").body()
    }

    override suspend fun searchCharacters(query: String): List<Character> {
        // Note: If the API doesn't support search, we'll need to fetch all and filter locally
        // For now, we'll implement a basic version that fetches and filters
        val response: PaginatedResponse<Character> =
            httpClient.get("$BASE_URL/characters") {
                parameter("limit", 100) // Fetch more to search through
            }.body()

        return response.items.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }

    // Planet endpoints
    override suspend fun fetchPlanets(page: Int, limit: Int): PaginatedResponse<Planet> {
        return httpClient.get("$BASE_URL/planets") {
            parameter("page", page)
            parameter("limit", limit)
        }.body()
    }

    override suspend fun fetchPlanet(planetId: Int): Planet {
        return httpClient.get("$BASE_URL/planets/$planetId").body()
    }

    override suspend fun searchPlanets(query: String): List<Planet> {
        val response: PaginatedResponse<Planet> =
            httpClient.get("$BASE_URL/planets") {
                parameter("limit", 100)
            }.body()

        return response.items.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }

    // Transformation endpoints
    // Note: The transformations API returns a direct array, not a paginated response
    override suspend fun fetchTransformations(): List<TransformationDetail> {
        return httpClient.get("$BASE_URL/transformations").body()
    }

    override suspend fun fetchTransformation(transformationId: Int): TransformationDetail {
        return httpClient.get("$BASE_URL/transformations/$transformationId").body()
    }

    override suspend fun searchTransformations(query: String): List<TransformationDetail> {
        val transformations: List<TransformationDetail> = httpClient.get(
            "$BASE_URL/transformations"
        ).body()

        return transformations.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }
}
