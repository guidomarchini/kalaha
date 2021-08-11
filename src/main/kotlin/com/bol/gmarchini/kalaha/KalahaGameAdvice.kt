package com.bol.gmarchini.kalaha

import com.bol.gmarchini.kalaha.domain.exceptions.InvalidMovementException
import com.bol.gmarchini.kalaha.service.exceptions.ErrorResponse
import com.bol.gmarchini.kalaha.service.exceptions.GameNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Defines the exceptions return http status code
 */
@ControllerAdvice
class KalahaGameAdvice {
    companion object {
        @JvmStatic
        private val logger: Logger = LoggerFactory.getLogger(KalahaGameAdvice::class.java)
    }

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun gameNotFoundHandler(exception: GameNotFoundException): ErrorResponse {
        logger.error(exception.toString(), exception)
        return ErrorResponse(exception.localizedMessage)
    }

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun invalidMovementHandler(exception: InvalidMovementException): ErrorResponse {
        logger.error(exception.toString(), exception)
        return ErrorResponse(exception.localizedMessage)
    }
}