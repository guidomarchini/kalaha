package com.bol.gmarchini.kalaha.application.controller

import com.bol.gmarchini.kalaha.application.dto.GameMovementDto
import com.bol.gmarchini.kalaha.application.dto.KalahaGameDto
import com.bol.gmarchini.kalaha.service.KalahaGameService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/kalaha")
class KalahaGameController constructor(
    private val kalahaGameService: KalahaGameService
) {
    @GetMapping
    fun getAllKalahaGames(): List<KalahaGameDto> {
        return this.kalahaGameService.getAll()
    }

    @GetMapping("/{gameId}")
    fun getKalahaGame(@PathVariable gameId: Int): KalahaGameDto {
        return this.kalahaGameService.getById(gameId)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createGame(): KalahaGameDto {
        return this.kalahaGameService.create()
    }

    @PutMapping("/{gameId}")
    fun move(
        @PathVariable gameId: Int,
        @RequestBody movement: GameMovementDto
    ): KalahaGameDto {
        return this.kalahaGameService.move(gameId, pitPosition = movement.pitPosition)
    }
}