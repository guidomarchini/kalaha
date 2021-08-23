# Code challenge - Kalaha Game 

Kalaha Game, by Guido Marchini.

Kalaha game is a table game in which we have two sides: South and North. Each side has 6 pits and a Kalaha.

Each turn a player grabs all of a pit's stones and leaves one in each of the following pits.

If a kalaha is reached:
* If it's own's Kalaha, then a stone is left there.
* If it's the opponent's Kalaha, then it's ignored

There are two special rules in this game:
* If the last stone lands:
  * On owner's Kalaha -> The player takes an extra turn.
  * On own's empty pit -> That stone and all the opposite pit's stones are moved into own Kalaha.
    
The game ends when all current player's pits are empty.
In that case, all stones are moved into owner's Kalaha.

## About the project
The project is done in Kotlin. I used spring-boot for creating the web application, jpa for persistence and Vaadin for frontend.

The project has three different modules:
* Model: Contains the shared model and DTO to communicate with the Backend.
* Backend: Contains the domain logic, and a KalahaController to create, get and make a movement.
* Frontend: The Vaadin application. Contains Auth logic and the game Views.

The most interesting part is on the backend:
* Domain: Contains the domain business logic. A GameManager that orchestrates the MovementManager for the moves, and GameOverManager for the game over trigger.
* Persistence: The persistence...
* Service: Is the intermediate between the application, persistence and domain.
* Application: Just the controller.

## How to run
The ideal project would be to split into 2 different projects: BE and FE.

Because of that, I decided to create two modules: backend and frontend, and they need to be run separately.

> mvn install
> 
> mvn -f backend/pom.xml spring-boot:run
>
> mvn -f frontend/pom.xml sprint-boot:run

Then you can enter `localhost:8081` for the UI, and `localhost:8080/kalaha` for the api.

### Api
* `GET /kalaha` gets all kalaha games.
* `GET /kalaha?username={username}` gets all kalaha games from the given player's username.
* `GET /kalaha/{id}` gets the kalaha game with the given id.
* `POST /kalaha` creates a kalaha game for the given players.
* `PUT /kalaha/{id}` executes a given kalaha's movement.

### UI

The system has 2 default users:
* username: `user`, password: `u` a user with USER role.
* username: `admin`, password: `a` a user with ADMIN role.

#### Login
The main site is a simple login. It takes you to HOME.

#### Home
It contains the games that the player is playing. On the right side you can challenge an opponent, creating a new game.

After creating a game, it will appear on the games list in this webpage, and it will be there for the challenged player, too.

By clicking on a game it will take you there.

#### Game
Here you can see the board with its current state. If you're the current player,
then you will be able to click on any available pit in order to execute a movement.

If not, then you will have to wait until your opponent makes his move.

#### Admin
Only accessed by users with ADMIN role.

The admin site contains the list of all the users, and a section to create a new one.

## TODOs
* Backend 

As the Backend and Frontend would be different projects,
we could rely on FE authentication, and the BE can whitelist a range of IPs
so it can only be reached by the frontend and via VPN.
    
* Frontend

New users creation is so simple...of course a real game would send an email or have oAuth.