package com.example.paypast.dto

import java.math.BigDecimal

data class AccountInfo(
    val userName: String,
    val balance: BigDecimal,
    val debt: BigDecimal,
    val debtor: String = ""
)


// for top up
data class TopupInfo(val name: String, val amount: BigDecimal)




