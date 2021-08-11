package com.bol.gmarchini.kalaha.service.exceptions

data class ErrorResponse (val errorMessage: String)

class GameNotFoundException(id: Int): Exception("Game with id $id not found")