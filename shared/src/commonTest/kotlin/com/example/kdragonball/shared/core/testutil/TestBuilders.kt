package com.example.kdragonball.shared.core.testutil

import com.example.kdragonball.shared.core.model.Links
import com.example.kdragonball.shared.core.model.Meta
import com.example.kdragonball.shared.core.model.PaginatedResponse
import com.example.kdragonball.shared.feature.character.domain.model.Character
import com.example.kdragonball.shared.feature.character.domain.model.OriginPlanet
import com.example.kdragonball.shared.feature.character.domain.model.Transformation
import com.example.kdragonball.shared.feature.planet.domain.model.Planet
import com.example.kdragonball.shared.feature.transformation.domain.model.TransformationDetail

/**
 * Test builders for creating domain model instances in tests.
 * Uses builder pattern for flexible test data creation.
 */
object TestBuilders {
    fun character(
        id: Int = 1,
        name: String = "Goku",
        ki: String = "60,000,000",
        maxKi: String = "90 Septillion",
        race: String = "Saiyan",
        gender: String = "Male",
        description: String = "El protagonista de la serie",
        image: String = "https://dragonball-api.com/characters/goku.webp",
        affiliation: String = "Z Fighter",
        originPlanet: OriginPlanet? = null,
        transformations: List<Transformation> = emptyList()
    ) = Character(
        id = id,
        name = name,
        ki = ki,
        maxKi = maxKi,
        race = race,
        gender = gender,
        description = description,
        image = image,
        affiliation = affiliation,
        originPlanet = originPlanet,
        transformations = transformations
    )

    fun originPlanet(
        id: Int? = 1,
        name: String = "Vegeta",
        isDestroyed: Boolean = true,
        description: String = "Planet of the Saiyans"
    ) = OriginPlanet(
        id = id,
        name = name,
        isDestroyed = isDestroyed,
        description = description
    )

    fun transformation(
        id: Int = 1,
        name: String = "Super Saiyan",
        image: String = "https://dragonball-api.com/transformations/ssj.webp",
        ki: String = "3 Billion"
    ) = Transformation(
        id = id,
        name = name,
        image = image,
        ki = ki
    )

    fun planet(
        id: Int = 1,
        name: String = "Earth",
        isDestroyed: Boolean = false,
        description: String = "Home planet of humans",
        image: String = "https://dragonball-api.com/planets/earth.webp",
        deletedAt: String? = null
    ) = Planet(
        id = id,
        name = name,
        isDestroyed = isDestroyed,
        description = description,
        image = image,
        deletedAt = deletedAt
    )

    fun transformationDetail(
        id: Int = 1,
        name: String = "Super Saiyan",
        image: String = "https://dragonball-api.com/transformations/ssj.webp",
        ki: String = "3 Billion",
        deletedAt: String? = null
    ) = TransformationDetail(
        id = id,
        name = name,
        image = image,
        ki = ki,
        deletedAt = deletedAt
    )

    fun <T> paginatedResponse(
        items: List<T>,
        totalItems: Int = items.size,
        currentPage: Int = 1,
        totalPages: Int = 1,
        itemsPerPage: Int = 20
    ) = PaginatedResponse(
        items = items,
        meta =
        Meta(
            totalItems = totalItems,
            itemCount = items.size,
            itemsPerPage = itemsPerPage,
            totalPages = totalPages,
            currentPage = currentPage
        ),
        links =
        Links(
            first = "/api/characters?page=1",
            previous = if (currentPage > 1) "/api/characters?page=${currentPage - 1}" else null,
            next = if (currentPage < totalPages) "/api/characters?page=${currentPage + 1}" else null,
            last = "/api/characters?page=$totalPages"
        )
    )

    /**
     * Creates a list of characters with sequential IDs
     */
    fun characterList(
        count: Int = 5,
        startId: Int = 1,
        namePrefix: String = "Character"
    ): List<Character> = (startId until startId + count).map { id ->
        character(
            id = id,
            name = "$namePrefix $id"
        )
    }

    /**
     * Creates a list of planets with sequential IDs
     */
    fun planetList(count: Int = 5, startId: Int = 1, namePrefix: String = "Planet"): List<Planet> =
        (startId until startId + count).map { id ->
            planet(
                id = id,
                name = "$namePrefix $id"
            )
        }

    /**
     * Creates a list of transformation details with sequential IDs
     */
    fun transformationDetailList(
        count: Int = 5,
        startId: Int = 1,
        namePrefix: String = "Transformation"
    ): List<TransformationDetail> = (startId until startId + count).map { id ->
        transformationDetail(
            id = id,
            name = "$namePrefix $id"
        )
    }
}
