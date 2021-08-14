package com.bol.gmarchini.kalaha.front.views

import com.bol.gmarchini.kalaha.front.service.AuthException
import com.bol.gmarchini.kalaha.front.service.AuthService
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouteAlias
import com.vaadin.flow.server.PWA

@Route("login")
@RouteAlias("/")
@CssImport("./styles/login-view.css")
@PWA(name = "Login for Kalaha Game", shortName = "Login")
class LoginView(private val authService: AuthService): KComposite() {
    private lateinit var usernameField: TextField
    private lateinit var passwordField: PasswordField
    private lateinit var loginButton: Button

    private val root: Component = ui {
        verticalLayout {

            className = "centered-content"
            h1("Welcome to Kalaha game")
            div {
                className = "login-view"
                usernameField = textField("Username")
                passwordField = passwordField("Password")
                loginButton = button ("Login") {
                    addClickShortcut(Key.ENTER)
                }
            }
        }
    }

    init {
        loginButton.onLeftClick {
            try {
                authService.authenticate(
                    username = usernameField.value,
                    password = passwordField.value
                )
                UI.getCurrent().navigate(HomeView::class.java)
            } catch (error: AuthException) {
                Notification.show(error.message)
            }
        }
    }
}