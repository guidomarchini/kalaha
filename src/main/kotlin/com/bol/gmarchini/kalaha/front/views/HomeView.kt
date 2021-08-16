package com.bol.gmarchini.kalaha.front.views

import com.bol.gmarchini.kalaha.front.entity.User
import com.bol.gmarchini.kalaha.front.service.UserService
import com.bol.gmarchini.kalaha.model.KalahaGame
import com.bol.gmarchini.kalaha.service.KalahaGameService
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.server.PWA
import com.vaadin.flow.server.VaadinSession

@CssImport("./styles/home-view.css")
@PWA(name = "Kalaha home", shortName = "Home")
class HomeView (
    private val kalahaGameService: KalahaGameService,
    private val userService: UserService
): KComposite(){
    private val currentUser: User = VaadinSession.getCurrent().getAttribute(User::class.java)
    private val games: MutableList<KalahaGame> = kalahaGameService.getGamesOfPlayer(currentUser.username).toMutableList()

    private lateinit var grid: Grid<KalahaGame>

    private val root = ui {
        splitLayout {
            setSizeFull()

            addToPrimary(PlayerGames())
            addToSecondary(NewGame())
        }
    }

    inner class PlayerGames: KComposite() {
        private val root = ui {
            div {
                setWidthFull()
                grid = grid<KalahaGame> {
                    addColumns(
                        "currentSide",
                        "southernPlayer",
                        "northernPlayer"
                    )
                    setItems(games)
                    addThemeVariants(GridVariant.LUMO_NO_BORDER)
                    setHeightFull()

                    asSingleSelect().addValueChangeListener { event ->
                        val selectedGame: KalahaGame = event.value
                        navigateToView(GameView::class, selectedGame.id)
                    }
                }
            }
        }
    }

    inner class NewGame: KComposite() {
        private lateinit var usersSelect: Select<User>
        private lateinit var challengeButton: Button

        private val root = ui {
            verticalLayout {
                div {
                    h1("Select a player to challenge")
                    usersSelect = select {
                        label = "Opponent"

                        val users: MutableList<User> = userService.findAll().toMutableList()
                        users.remove(currentUser)

                        setItems(users)
                        setItemLabelGenerator(User::username)
                    }
                    challengeButton = button {
                        text = "Challenge"
                        addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                    }
                }
            }
        }

        init {
            challengeButton.onLeftClick {
                try {
                    val challengedUser: User = usersSelect.value
                    val shuffledUsernames: List<String> =
                        listOf(challengedUser.username, currentUser.username).shuffled()
                    val newGame: KalahaGame = kalahaGameService.create(
                        southernPlayer = shuffledUsernames[0],
                        northernPlayer = shuffledUsernames[1]
                    )
                    games.add(0, newGame)
                    grid.refresh()

                    Notification.show("Game created successfully")
                } catch (error: Exception) {
                    Notification.show(error.message)
                }
            }
        }
    }
}