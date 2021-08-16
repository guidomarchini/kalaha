package com.bol.gmarchini.views.layout

import com.bol.gmarchini.kalaha.service.AuthService
import com.bol.gmarchini.kalaha.service.AuthorizedRoute
import com.bol.gmarchini.kalaha.views.LoginView
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.ComponentUtil
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import com.vaadin.flow.router.RouterLink

@CssImport("./styles/shared.css")
class MainLayout(private val authService: AuthService): AppLayout() {
    private lateinit var menu: Tabs

    init {
        primarySection = Section.DRAWER
        addToNavbar(true, HeaderComponent())
        addToDrawer(DrawerContent())
    }

    inner class HeaderComponent: KComposite() {
        private val root = ui {
            horizontalLayout {
                className = "sidemenu-header"
                themeList.set("dark", true)
                setWidthFull()
                isSpacing = false
                alignItems = FlexComponent.Alignment.CENTER
                add(DrawerToggle())

                button {
                    text = "logout"
                    className = "logout"
                    addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                    onLeftClick {
                        authService.logout()
                        UI.getCurrent().navigate(LoginView::class.java)
                    }
                }
            }
        }
    }

    inner class DrawerContent: KComposite() {
        private val root = ui {
            verticalLayout {
                themeList.set("dark", true)
                className = "sidemenu-menu"
                setSizeFull()
                isSpacing = false
                isPadding = false
                alignItems = FlexComponent.Alignment.STRETCH
                horizontalLayout {
                    h1("Kalaha")
                }
                add(createMenu())
            }
        }

        private fun createMenu(): Tabs {
            val tabs: Tabs = Tabs()
            tabs.orientation = Tabs.Orientation.VERTICAL
            tabs.setId("tabs")

            authService.routesForCurrentUser().forEach {
                tabs.add(Tab(createTab(it)))
            }

            return tabs
        }

        private fun createTab(route: AuthorizedRoute): Tab {
            val tab: Tab = Tab()
            val link: RouterLink = RouterLink()
            link.setRoute(route.view)
            link.add(route.name)
            tab.add(link)
            ComponentUtil.setData(tab, route.route, route.view)

            return tab
        }
    }
}