package com.example.paypast.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.text.SimpleDateFormat

const val dateFormat = "yyyy-MM-dd'T'HH:mm:ss"

fun getMapperWithDateFormat(): ObjectMapper {
    return jacksonObjectMapper().setDateFormat(SimpleDateFormat(dateFormat))
}