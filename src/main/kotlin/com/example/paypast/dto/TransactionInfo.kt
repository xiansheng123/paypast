package com.example.paypast.dto

import com.example.paypast.util.getMapperWithDateFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class TransactionInfo(
    val userName: String,
    val action: String,
    val transactionDetail: TransactionDetail?,
    val transactionDate: Date
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TransactionDetail(
    val totalAmount: BigDecimal,
    val transactionRecordList: List<TransactionRecord>
) {
    fun toJsonString(): String {
        return getMapperWithDateFormat().writeValueAsString(this)
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class TransactionRecord(
    val receiver: String,
    val amount: BigDecimal,
    //val DebtAmount: BigDecimal
)