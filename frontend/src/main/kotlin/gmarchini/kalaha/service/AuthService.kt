package gmarchini.kalaha.service

import gmarchini.kalaha.entity.Role
import gmarchini.kalaha.entity.User
import gmarchini.kalaha.views.AdminView
import gmarchini.kalaha.views.GameView
import gmarchini.kalaha.views.HomeView
import gmarchini.views.layout.MainLayout
import com.github.mvysny.karibudsl.v10.KComposite
import com.vaadin.flow.router.RouteConfiguration
import com.vaadin.flow.server.VaadinSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AuthService @Autowired constructor(
    private val userService: UserService
) {

    /**
     * Authenticates the user for the given username and password.
     * This stores the user into current session and creates the routes for that user.
     */
    fun authenticate(username: String, password: String): Unit {
        val user: User? = userService.getUser(username)
        if (user != null && user.checkPassword(password)) {
            VaadinSession.getCurrent().setAttribute(User::class.java, user)
            createRoutes(user.role)
        } else {
            throw AuthException()
        }
    }

    /**
     * Returns the routes for the currently logged user.
     * Used in DrawerContent.
     */
    fun routesForCurrentUser(): List<AuthorizedRoute> =
        getRoutes(VaadinSession.getCurrent().getAttribute(User::class.java).role)

    /**
     * Logs out the current user
     */
    fun logout(): Unit {
        VaadinSession.getCurrent().session.invalidate()
        VaadinSession.getCurrent().close()
    }

    /**
     * Gives the current user the permission to enter the routes given by its Role.
     */
    private fun createRoutes(role: Role): Unit {
        getRoutes(role).forEach { authorizedRoute ->
            RouteConfiguration.forSessionScope().setRoute(
                authorizedRoute.route, authorizedRoute.view, MainLayout::class.java
            )
        }

        // additionally create permission to GameView and logout
        RouteConfiguration.forSessionScope().setRoute(
            "game", GameView::class.java, MainLayout::class.java
        )
    }

    private fun getRoutes(role: Role): List<AuthorizedRoute> = when(role) {
        Role.USER ->
            listOf(
                AuthorizedRoute("home", "Home", HomeView::class.java)
            )
        Role.ADMIN ->
            listOf(
                AuthorizedRoute("home", "Home", HomeView::class.java),
                AuthorizedRoute("admin", "Admin", AdminView::class.java)
            )
    }
}

data class AuthorizedRoute(
    val route: String,
    val name: String,
    val view: Class<out KComposite>
)