package gmarchini.kalaha.application

import gmarchini.kalaha.dto.GameMovementDto
import gmarchini.kalaha.dto.NewGameDto
import gmarchini.kalaha.model.KalahaGame
import gmarchini.kalaha.service.KalahaGameService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/kalaha")
class KalahaGameController constructor(
    private val kalahaGameService: KalahaGameService
) {
    @GetMapping
    fun getAllKalahaGames(@RequestParam(required = false) username: String?): List<KalahaGame> {
        return if (username != null)
            this.kalahaGameService.getGamesOfPlayer(username)
        else
            this.kalahaGameService.getAll()
    }

    @GetMapping("/{gameId}")
    fun getKalahaGame(@PathVariable gameId: Int): KalahaGame {
        return this.kalahaGameService.getById(gameId)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createGame(@RequestBody newGameDto: NewGameDto): KalahaGame {
        return this.kalahaGameService.create(
            southernPlayer = newGameDto.southernPlayer,
            northernPlayer = newGameDto.northernPlayer
        )
    }

    @PutMapping("/{gameId}")
    fun move(
        @PathVariable gameId: Int,
        @RequestBody movement: GameMovementDto
    ): KalahaGame {
        return this.kalahaGameService.move(gameId, pitPosition = movement.pitPosition, movement.executingPlayer)
    }
}