package gmarchini.kalaha.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class NewGameDto (
    @JsonProperty("southernPlayer") val southernPlayer: String,
    @JsonProperty("northernPlayer") val northernPlayer: String
)