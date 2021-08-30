package gmarchini.kalaha.service.exceptions

data class ErrorResponse (val errorMessage: String)

class GameNotFoundException(id: Int): Exception("Game with id $id not found")
class UnauthorizedMovement(username: String): Exception("User $username is not authorized to make a movement")