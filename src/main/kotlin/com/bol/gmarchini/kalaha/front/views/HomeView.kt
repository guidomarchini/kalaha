package com.bol.gmarchini.kalaha.front.views

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.server.PWA

@CssImport("./styles/home-view.css")
@PWA(name = "Kalaha home", shortName = "Home")
class HomeView: KComposite(){
    private val root = ui {
        verticalLayout {
            text("Home in progress")
        }
    }
}