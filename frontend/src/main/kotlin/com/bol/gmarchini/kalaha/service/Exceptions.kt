package com.bol.gmarchini.kalaha.service

class AuthException: Exception("Please check the entered data")

class UserAlreadyExistsException(username: String): Exception("User $username already exists")
class EmptyUserException: Exception("User name and password can't be empty")