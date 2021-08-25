package com.example.paypast.entity

import com.example.paypast.dto.AccountInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "USER_MAIN_ACCOUNT_SUMMARY")
data class UserMainAccountSummaryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val userName: String,
    val depositsBalance: BigDecimal,
    val outstandingDebt: BigDecimal,/* 1 -100: owing to others 100; 2 +100: owing from others */
    val debtor: String = "",
    val updateOn: Date = Date(),
    val updatedBy: String
) {
    fun toModel(): AccountInfo {

        return AccountInfo(
            userName = userName,
            balance = depositsBalance,
            debt = outstandingDebt,
            debtor = debtor
        )
    }
}


@Repository
interface UserMainAccountSummaryRepository : JpaRepository<UserMainAccountSummaryEntity, Int> {
    fun findByUserName(name: String): UserMainAccountSummaryEntity
}