package com.bol.gmarchini.kalaha.service

import com.bol.gmarchini.kalaha.dto.GameMovementDto
import com.bol.gmarchini.kalaha.dto.NewGameDto
import com.bol.gmarchini.kalaha.model.KalahaGame
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class KalahaGameService {
    private val httpClient: HttpClient = HttpClient.newBuilder().build()
    private val gson: Gson = Gson()

    fun move(gameId: Int, pitPosition: Int, executingPlayer: String): KalahaGame {
        val payload: GameMovementDto = GameMovementDto(executingPlayer, pitPosition)
        val payloadAsString: String = gson.toJson(payload)
        val uri: URI = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/kalaha/$gameId").build().toUri()

        val request: HttpRequest = HttpRequest.newBuilder()
            .PUT(HttpRequest.BodyPublishers.ofString(payloadAsString))
            .header("content-type", "application/json")
            .uri(uri)
            .build()

        val responseBody =
            this.httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body()

        // little hack to return list of T
        return gson.fromJson(responseBody, KalahaGame::class.java)
    }

    fun getById(gameId: Int): KalahaGame? {
        val uri: URI = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/kalaha/$gameId").build().toUri()
        val request: HttpRequest = HttpRequest.newBuilder()
            .GET()
            .uri(uri)
            .build()

        val responseBody =
            this.httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body()

        // little hack to return list of T
        return gson.fromJson(responseBody, KalahaGame::class.java)
    }

    fun getGamesOfPlayer(username: String): List<KalahaGame> {
        val uri: URI = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/kalaha").build().toUri()
        val request: HttpRequest = HttpRequest.newBuilder()
            .GET()
            .uri(uri)
            .build()

        val responseBody =
            this.httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body()

        // little hack to return list of T
        val listType = object: TypeToken<List<KalahaGame>>() {}.type
        return gson.fromJson(responseBody, listType)
    }

    fun create(southernPlayer: String, northernPlayer: String): KalahaGame {
        val payload: NewGameDto = NewGameDto(southernPlayer, northernPlayer)
        val payloadAsString: String = gson.toJson(payload)
        val uri: URI = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/kalaha").build().toUri()

        val request: HttpRequest = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(payloadAsString))
            .header("content-type", "application/json")
            .uri(uri)
            .build()

        val responseBody =
            this.httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body()

        // little hack to return list of T
        return gson.fromJson(responseBody, KalahaGame::class.java)
    }

}