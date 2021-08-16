package com.bol.gmarchini.kalaha.application

import com.bol.gmarchini.kalaha.dto.GameMovementDto
import com.bol.gmarchini.kalaha.dto.NewGameDto
import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.persistence.KalahaGameRepository
import com.bol.gmarchini.kalaha.persistence.entity.KalahaGameEntity
import com.bol.gmarchini.kalaha.utils.KalahaGameEntityBuilder
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

    @Nested
    inner class GetGames {
        @Test
        fun `get all kalaha games`() {
            // arrange
            val kalahaGame1: KalahaGameEntity = generateGame()
            val kalahaGame2: KalahaGameEntity = generateGame()

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

        @Test
        fun `get games for a given player`() {
            // arrange
            val player1: String = "player1"
            val player2: String = "player2"
            val player3: String = "player3"
            val gameAsSouthernPlayer: KalahaGameEntity = generateGame(
                southernPlayer = player1,
                northernPlayer = player2
            )
            val gameAsNorthernPlayer: KalahaGameEntity = generateGame(
                southernPlayer = player3,
                northernPlayer = player1
            )
            generateGame(
                southernPlayer = player2,
                northernPlayer = player3
            )

            // act
            given()
                .get("/kalaha?username={username}", player1)
                .then()
                // assert
                .statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.equalTo(2))
                .body("find { it.id == ${gameAsSouthernPlayer.id} }", Matchers.notNullValue())
                .body("find { it.id == ${gameAsNorthernPlayer.id} }", Matchers.notNullValue())
        }
    }

    @Nested
    inner class GetGame() {
        @Test
        fun `get a specific kalaha game`() {
            // arrange
            val kalahaGame: KalahaGameEntity = generateGame()

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
        // arrange
        val payload: NewGameDto = NewGameDto("southernPlayer", "northernPlayer")

        // act
        val newGameId: Int = given()
            .body(payload)
            .contentType(ContentType.JSON)
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
        private fun generateInitialGame(): KalahaGameEntity {
            return generateGame(southernPits = intArrayOf(2, 0, 4, 8), northernPits = intArrayOf(6, 6, 6, 6))
        }

        @Test
        fun `generate a movement`() {
            // arrange
            val generatedGame: KalahaGameEntity = generateInitialGame()
            val movementDto: GameMovementDto = GameMovementDto(pitPosition = 0, executingPlayer = generatedGame.southernPlayer)

            // act
            given()
                .body(movementDto)
                .contentType(ContentType.JSON)
                .put("/kalaha/{id}", generatedGame.id)
                .then()
                // assert
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.equalTo(generatedGame.id))

            val gameAfterMovement: KalahaGameEntity? = repository.findByIdOrNull(generatedGame.id!!)
            assertThat(gameAfterMovement).isNotNull
            assertThat(gameAfterMovement!!.southernPits).isEqualTo(intArrayOf(0, 1, 5, 8))
            assertThat(gameAfterMovement.northernPits).isEqualTo(intArrayOf(6, 6, 6, 6))
            assertThat(gameAfterMovement.southernKalaha).isEqualTo(0)
            assertThat(gameAfterMovement.northernKalaha).isEqualTo(0)
        }

        @Test
        fun `move on an invalid pit`() {
            // arrange
            val generatedGame: KalahaGameEntity = generateInitialGame()
            val movementDto: GameMovementDto = GameMovementDto(pitPosition = 1, executingPlayer = generatedGame.southernPlayer)

            // act
            given()
                .body(movementDto)
                .contentType(ContentType.JSON)
                .put("/kalaha/{id}", generatedGame.id)
                .then()
                // assert
                .statusCode(HttpStatus.BAD_REQUEST.value())
        }

        @Test
        fun `another player tries to move`() {
            // arrange
            val generatedGame: KalahaGameEntity = generateInitialGame()
            val movementDto: GameMovementDto = GameMovementDto(pitPosition = 1, executingPlayer = "another player")

            // act
            given()
                .body(movementDto)
                .contentType(ContentType.JSON)
                .put("/kalaha/{id}", generatedGame.id)
                .then()
                // assert
                .statusCode(HttpStatus.BAD_REQUEST.value())
        }
    }


    fun generateGame(
        southernPlayer: String = "southernPlayer",
        northernPlayer: String = "northernPlayer",
        northernPits: IntArray = intArrayOf(7, 7, 7),
        southernPits: IntArray = intArrayOf(7, 7, 7)
    ): KalahaGameEntity {
        val kalahaGame: KalahaGameEntity = KalahaGameEntityBuilder.sampleKalahaGameEntity(
            currentSide = Side.SOUTH,
            southernPlayer = southernPlayer,
            northernPlayer = northernPlayer,
            southernPits = southernPits,
            northernPits = northernPits,
        )

        return repository.save(kalahaGame)
    }
}