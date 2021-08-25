package com.example.paypast.entity

import com.example.paypast.dto.TransactionDetail
import com.example.paypast.dto.TransactionInfo
import com.example.paypast.util.getMapperWithDateFormat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "TRANSACTION_SUMMARY")
data class TransactionSummaryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val fromUser: String = "",
    val toUser: String = "",
    val action: String = "",
    val transactionDetail: String = "{}",
    val updateOn: Date = Date(),
    val updatedBy: String,
) {
    fun toModel(): TransactionInfo {
        return TransactionInfo(
            userName = fromUser,
            action = action,
            transactionDetail = getMapperWithDateFormat().readValue(transactionDetail, TransactionDetail::class.java),
            transactionDate = updateOn
        )
    }
}

@Repository
interface TransactionSummaryRepository : JpaRepository<TransactionSummaryEntity, Int> {
}