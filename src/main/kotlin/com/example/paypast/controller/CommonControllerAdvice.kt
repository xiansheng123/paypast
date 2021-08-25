package com.example.paypast.controller

import com.example.paypast.exception.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class CommonControllerAdvice {
    private val AUTH_ERROR = "your bankID and/or password is incorrect. please try again";
    private val logger = LoggerFactory.getLogger(CommonControllerAdvice::class.java)

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    @ExceptionHandler(ValidationException::class)
    fun authException(e: ValidationException): ApiError {
        logger.error("Validation Exception:", e)
        return ApiError(AUTH_ERROR, e.content);
    }
}

data class ApiError(val group: String, val content: String)