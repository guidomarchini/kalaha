package com.bol.gmarchini.kalaha.application.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GameMovementDto (
    @JsonProperty("pitPosition") val pitPosition: Int
)