package com.bol.gmarchini.kalaha.front.views

import com.bol.gmarchini.kalaha.front.entity.User
import com.bol.gmarchini.kalaha.model.KalahaGame
import com.bol.gmarchini.kalaha.model.Side
import com.bol.gmarchini.kalaha.service.KalahaGameService
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.router.BeforeEvent
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.server.PWA
import com.vaadin.flow.server.VaadinSession

@CssImport("./styles/game-view.css")
@PWA(name = "Kalaha game", shortName = "Game")
class GameView (private val gameService: KalahaGameService): KComposite(), HasUrlParameter<Int> {
    private val currentUser: User = VaadinSession.getCurrent().getAttribute(User::class.java)
    private lateinit var tableContainer: Div

    private val root = ui {
        verticalLayout {

            setWidthFull()
            h1 {
                text = "Northern Player"
                setWidthFull()
            }

            h3 {
                className = "kalaha-text"
                text = "NorthernKalaha"
            }
            tableContainer = div()
            h3 {
                className = "kalaha-text"
                text = "SouthernKalaha"
            }


            h1 {
                text = "SouthernPlayer"
                setWidthFull()
            }
        }
    }

    inner class Table(game: KalahaGame): KComposite() {
        private val root = ui {
            horizontalLayout {
                className = "kalaha-container"
                setWidthFull()
                alignItems = FlexComponent.Alignment.CENTER

                label(game.table.getKalaha(Side.NORTH).toString())
                div {
                    className = "table"

                    add(Pits(game, Side.NORTH))
                    add(Pits(game, Side.SOUTH))

                }
                label {
                    text = game.table.getKalaha(Side.SOUTH).toString()
                }
            }
        }
    }

    inner class Pits(val game: KalahaGame, val side: Side): KComposite() {
        private val root = ui {
            flexLayout {
                val isCurrentUser: Boolean = currentUser.username == game.currentPlayer()
                val currentPlayerTurn: Boolean = isCurrentUser && game.currentSide == side

                val buttons = game.table.getPits(side).mapIndexed { index, rocks ->
                    button {
                        text = rocks.toString()
                        if(currentPlayerTurn && rocks > 0) {
                            className = "pit pit-enabled"
                            onLeftClick {
                                move(game.id!!, index)
                            }
                        } else {
                            className = "pit pit-disabled"
                            isEnabled = false
                        }
                    }
                }
                (if (side == Side.NORTH) buttons.reversed() else buttons)
                    .forEach{ this.add(it) }
            }
        }
    }

    private fun move(gameId: Int, index: Int) {
        try {
            gameService.move(
                gameId = gameId,
                pitPosition = index,
                executingPlayer = currentUser.username
            )
            UI.getCurrent().page.reload()
        } catch (error: Exception) {
            Notification(error.message)
        }
    }

    override fun setParameter(event: BeforeEvent?, gameId: Int?) {
        val game: KalahaGame? = gameId?.let { gameService.getById(it) }
        val currentUser: User = VaadinSession.getCurrent().getAttribute(User::class.java)

        // validate
        if (game == null || currentUser.username != game.northernPlayer && currentUser.username != game.southernPlayer) {
            throw Exception("can't find the game")
        }

        tableContainer.add(Table(game))
    }
}