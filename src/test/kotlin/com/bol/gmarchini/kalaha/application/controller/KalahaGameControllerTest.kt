package com.bol.gmarchini.kalaha.application.controller

import com.bol.gmarchini.kalaha.application.dto.GameMovementDto
import com.bol.gmarchini.kalaha.application.dto.KalahaGameDto
import com.bol.gmarchini.kalaha.domain.GameOverManager
import com.bol.gmarchini.kalaha.domain.KalahaGame
import com.bol.gmarchini.kalaha.domain.MovementManager
import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.model.Table
import com.bol.gmarchini.kalaha.persistence.KalahaGameRepository
import com.bol.gmarchini.kalaha.persistence.entities.KalahaGameEntity
import com.bol.gmarchini.kalaha.service.KalahaGameService
import com.bol.gmarchini.kalaha.service.mappers.KalahaGameMapper
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class KalahaGameControllerTest (
    @Autowired val repository: KalahaGameRepository,
    @Autowired val service: KalahaGameService,
    @Autowired val mapper: KalahaGameMapper,
    @LocalServerPort val port: Int
) {
    @BeforeAll
    fun beforeAll() {
        RestAssured.port = port
    }

    @AfterEach
    fun afterEach() {
        repository.deleteAll()
    }

    @Test
    fun `get all kalaha games`() {
        // arrange
        val kalahaGame1: KalahaGameDto = generateGame()
        val kalahaGame2: KalahaGameDto = generateGame()

        // act
        given()
            .get("/kalaha")
            .then()
            // assert
            .statusCode(HttpStatus.OK.value())
            .body("size()", Matchers.equalTo(2))
            .body("find { it.id == ${kalahaGame1.id} }", Matchers.notNullValue())
            .body("find { it.id == ${kalahaGame2.id} }", Matchers.notNullValue())
    }

    @Nested
    inner class GetGame() {
        @Test
        fun `get a specific kalaha game`() {
            // arrange
            val kalahaGame: KalahaGameDto = generateGame()

            // act
            given()
                .get("/kalaha/{id}", kalahaGame.id)
                .then()
                // assert
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.equalTo(kalahaGame.id))
        }

        @Test
        fun `get a non existent game`() {
            // arrange
            val id: Int = 1

            // act
            given()
                .get("/kalaha/{id}", id)
                .then()
                // assert
                .statusCode(HttpStatus.NOT_FOUND.value())
        }
    }

    @Test
    fun `create a new game`() {
        // arrange - nothing
        // act
        val newGameId: Int = given()
            .post("/kalaha")
            .then()
            // assert
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .path("id")

        val savedGame = repository.findById(newGameId)
        assertThat(savedGame).isNotNull
    }

    @Nested
    inner class GenerateMove() {
        /**
         * Table would be like
         *          N
         *   | 6 | 6 | 6 | 6 | <-
         *   |---------------|
         * ->| 2 | 0 | 4 | 8 |
         *          S
         *  South Kalaha: 0
         *  North Kalaha: 0
         */
        private fun generateInitialGame(): Int {
            val table = Table.restore(
                southernPits = mutableListOf(2, 0, 4, 8),
                northernPits = mutableListOf(6, 6, 6, 6),
                southernKalaha = 0,
                northernKalaha = 0
            )

            val kalahaGame: KalahaGame =  KalahaGame.restore(
                table,
                currentPlayer = Side.SOUTH,
                MovementManager(),
                GameOverManager()
            )

            return repository.save(mapper.toEntity(kalahaGame)).id!!
        }

        @Test
        fun `generate a movement`() {
            // arrange
            val gameId: Int = generateInitialGame()
            val movementDto: GameMovementDto = GameMovementDto(pitPosition = 0)

            // act
            given()
                .body(movementDto)
                .contentType(ContentType.JSON)
                .put("/kalaha/{id}", gameId)
                .then()
                // assert
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.equalTo(gameId))

            val gameAfterMovement: KalahaGameEntity? = repository.findByIdOrNull(gameId)
            assertThat(gameAfterMovement).isNotNull
            assertThat(gameAfterMovement?.southernPits).isEqualTo(intArrayOf(0, 1, 5, 8))
            assertThat(gameAfterMovement?.northernPits).isEqualTo(intArrayOf(6, 6, 6, 6))
            assertThat(gameAfterMovement?.southernKalaha).isEqualTo(0)
            assertThat(gameAfterMovement?.northernKalaha).isEqualTo(0)
        }

        @Test
        fun `move on an invalid pit`() {
            // arrange
            val gameId: Int = generateInitialGame()
            val movementDto: GameMovementDto = GameMovementDto(pitPosition = 1)

            // act
            given()
                .body(movementDto)
                .contentType(ContentType.JSON)
                .put("/kalaha/{id}", gameId)
                .then()
                // assert
                .statusCode(HttpStatus.BAD_REQUEST.value())
        }
    }


    fun generateGame(): KalahaGameDto =
        service.create()
}