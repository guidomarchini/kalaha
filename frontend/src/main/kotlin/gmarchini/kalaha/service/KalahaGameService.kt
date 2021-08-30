package gmarchini.kalaha.service

import gmarchini.kalaha.dto.GameMovementDto
import gmarchini.kalaha.dto.NewGameDto
import gmarchini.kalaha.model.KalahaGame
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class KalahaGameService (
    @Value("\${kalaha.backend.endpoint}") private val endpoint: String,
    @Value("\${kalaha.backend.port}") private val port: String
){
    companion object {
        @JvmStatic
        private val logger: Logger = LoggerFactory.getLogger(KalahaGameService::class.java)
    }
    private val httpClient: HttpClient = HttpClient.newBuilder().build()
    private val gson: Gson = Gson()

    /**
     * Gets all the games for the given username.
     */
    fun getGamesOfPlayer(username: String): List<KalahaGame> {
        val uri: URI = UriComponentsBuilder.fromHttpUrl("http://{endpoint}:{port}/kalaha").build(endpoint, port)
        val request: HttpRequest = HttpRequest.newBuilder()
            .GET()
            .uri(uri)
            .build()

        val response =
            this.httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() != HttpStatus.OK.value()) {
            logger.error("got an error Response from the server: ${response.body()}")
            throw Error()
        }

        // little hack to return list of T
        val listType = object: TypeToken<List<KalahaGame>>() {}.type
        return gson.fromJson(response.body(), listType)
    }

    /**
     * Creates a new game for the given players.
     */
    fun create(southernPlayer: String, northernPlayer: String): KalahaGame {
        val payload: NewGameDto = NewGameDto(southernPlayer, northernPlayer)
        val payloadAsString: String = gson.toJson(payload)
        val uri: URI = UriComponentsBuilder.fromHttpUrl("http://{endpoint}:{port}/kalaha").build(endpoint, port)

        val request: HttpRequest = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(payloadAsString))
            .header("content-type", "application/json")
            .uri(uri)
            .build()

        val response =
            this.httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() != HttpStatus.CREATED.value()) {
            logger.error("got an error Response from the server: ${response.body()}")
            throw Error()
        }

        // little hack to return list of T
        return gson.fromJson(response.body(), KalahaGame::class.java)
    }

    /**
     * Fetches the Kalaha game for the given id.
     */
    fun getById(gameId: Int): KalahaGame? {
        val uri: URI = UriComponentsBuilder.fromHttpUrl("http://{endpoint}:{port}/kalaha/$gameId").build(endpoint, port)
        val request: HttpRequest = HttpRequest.newBuilder()
            .GET()
            .uri(uri)
            .build()

        val response =
            this.httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() != HttpStatus.OK.value()) {
            logger.error("got an error Response from the server: ${response.body()}")
            throw Error()
        }

        // little hack to return list of T
        return gson.fromJson(response.body(), KalahaGame::class.java)
    }

    /**
     * Generates a game movement.
     * @param gameId the game where the movement will be done.
     * @param pitPosition the pit where the movement will be done.
     * @param executingPlayer the player that executes the movement.
     * @return the Kalaha game after the movement.
     */
    fun move(gameId: Int, pitPosition: Int, executingPlayer: String): KalahaGame {
        val payload: GameMovementDto = GameMovementDto(executingPlayer, pitPosition)
        val payloadAsString: String = gson.toJson(payload)
        val uri: URI = UriComponentsBuilder.fromHttpUrl("http://{endpoint}:{port}/kalaha/$gameId").build(endpoint, port)

        val request: HttpRequest = HttpRequest.newBuilder()
            .PUT(HttpRequest.BodyPublishers.ofString(payloadAsString))
            .header("content-type", "application/json")
            .uri(uri)
            .build()

        val response =
            this.httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() != HttpStatus.OK.value()) {
            logger.error("got an error Response from the server: ${response.body()}")
            throw Error()
        }

        // little hack to return list of T
        return gson.fromJson(response.body(), KalahaGame::class.java)
    }
}