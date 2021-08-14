package com.bol.gmarchini.kalaha.front.views

import com.bol.gmarchini.kalaha.front.entity.Role
import com.bol.gmarchini.kalaha.front.entity.User
import com.bol.gmarchini.kalaha.front.service.UserService
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.server.PWA

@CssImport("./styles/admin-view.css")
@PWA(name = "Kalaha admin", shortName = "Admin")
class AdminView(private val userService: UserService): KComposite() {
    private lateinit var grid: Grid<User>

    // can't find a way to refresh users :( TODO add DataProvider...
    private val users: MutableList<User> = userService.findAll().toMutableList()

    private val root = ui {
        splitLayout {
            setSizeFull()

            addToPrimary(UserTable())
            addToSecondary(NewUser())
        }
    }

    inner class UserTable: KComposite() {
        private val root = ui {
            div {
                setWidthFull()
                grid = grid(User::class.java) {
                    addColumns(
                        "username",
                        "role"
                    )
                    setItems(users)
                    addThemeVariants(GridVariant.LUMO_NO_BORDER)
                    setHeightFull()
                }
            }
        }
    }

    inner class NewUser : KComposite() {
        private lateinit var newUserName: TextField
        private lateinit var newUserPassword: PasswordField
        private lateinit var newUserRole: Select<Role>

        private lateinit var saveButton: Button

        private val root = ui {
            div {
                className = "flex flex-col"
                width = "400px"

                div {
                    className = "p-l flex-grow"

                    formLayout {
                        newUserName = textField("Username")
                        newUserPassword = passwordField("Password")
                        newUserRole = select {
                            setItems(Role.values().toList())
                            label = "Role"
                        }
                    }
                }

                saveButton = button {
                    text = "Save"
                    addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                }
            }
        }

        init {
            saveButton.onLeftClick {
                try {
                    val newUser: User = userService.create(newUserName.value, newUserPassword.value, newUserRole.value)
                    users.add(newUser)
                    grid.refresh()

                    Notification.show("User saved.")
                    UI.getCurrent().navigate(AdminView::class.java)
                } catch (error: Exception) {
                    Notification.show(error.message)
                }
            }
        }
    }
}