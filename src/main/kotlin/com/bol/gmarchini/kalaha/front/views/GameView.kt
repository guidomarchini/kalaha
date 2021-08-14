package com.bol.gmarchini.kalaha.front.views

import com.github.mvysny.karibudsl.v10.KComposite
import com.github.mvysny.karibudsl.v10.text
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.server.PWA

@CssImport("./styles/game-view.css")
@PWA(name = "Kalaha game", shortName = "Game")
class GameView: KComposite() {
    private val root = ui {
        verticalLayout {
            text("Game in progress")
        }
    }
}