package com.bol.gmarchini.kalaha.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GameMovementDto (
    @JsonProperty("executingPlayer") val executingPlayer: String,
    @JsonProperty("pitPosition") val pitPosition: Int
)