package com.example.paypast.service

import com.example.paypast.dto.TopupInfo
import com.example.paypast.entity.TransactionSummaryEntity
import com.example.paypast.entity.TransactionSummaryRepository
import com.example.paypast.entity.UserMainAccountSummaryEntity
import com.example.paypast.entity.UserMainAccountSummaryRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal

@ExtendWith(MockKExtension::class)
internal class AccountServiceTest {
    @MockK
    lateinit var userService: UserService

    @MockK
    lateinit var mainAccountSummaryRepo: UserMainAccountSummaryRepository

    @MockK
    lateinit var transactionSummaryRepo: TransactionSummaryRepository

    @InjectMockKs
    lateinit var accountService: AccountService

    @Nested
    @DisplayName("topup function test")
    inner class topup {
        @Test
        fun topupForNoDebtUser() {
            val name = "Bob"
            val topupInfo = TopupInfo(name = name, amount = BigDecimal(100))
            val zero = BigDecimal.ZERO
            every { mainAccountSummaryRepo.findByUserName(name) } returns
                    UserMainAccountSummaryEntity(
                        id = 1, userName = name, depositsBalance = zero, outstandingDebt = zero,
                        updatedBy = name
                    )
            every { mainAccountSummaryRepo.save(any()) } returns createMainAccount()

            every {
                transactionSummaryRepo.save(any())
            } returns createTransactionSummaryEntity()


            val result = accountService.topUpMoney(topupInfo)
            assertThat(result.userName).isEqualTo(name)
            assertThat(result.balance).isEqualTo(BigDecimal(100))
            assertThat(result.debt).isEqualTo(zero)
            assertThat(result.debtor).isEmpty()
        }

        @Test
        fun topupForDebtUser() {
            val name = "Bob"
            val topupInfo = TopupInfo(name = name, amount = BigDecimal(100))
            val zero = BigDecimal.ZERO
            val receiver = "Alice"
            every { mainAccountSummaryRepo.findByUserName(name) } returns
                    UserMainAccountSummaryEntity(
                        id = 1, userName = name, depositsBalance = zero, outstandingDebt = BigDecimal(-90),
                        debtor = receiver,
                        updatedBy = name
                    )
            every { mainAccountSummaryRepo.findByUserName(receiver) } returns
                    UserMainAccountSummaryEntity(
                        id = 2, userName = receiver, depositsBalance = zero, outstandingDebt = BigDecimal(90),
                        debtor = "",
                        updatedBy = name
                    )
            every { mainAccountSummaryRepo.save(any()) } returns createMainAccount()

            every {
                transactionSummaryRepo.save(any())
            } returns createTransactionSummaryEntity()
            val result = accountService.topUpMoney(topupInfo)
            assertThat(result.userName).isEqualTo(name)
            assertThat(result.balance).isEqualTo(BigDecimal(10))
            assertThat(result.debt).isEqualTo(zero)
            assertThat(result.debtor).isEmpty()
        }

        private fun createMainAccount(): UserMainAccountSummaryEntity {
            val zero = BigDecimal.ZERO
            return UserMainAccountSummaryEntity(
                id = 1, userName = "Bob", depositsBalance = zero, outstandingDebt = zero,
                updatedBy = "Bob"
            )
        }

        private fun createTransactionSummaryEntity(): TransactionSummaryEntity {
            return TransactionSummaryEntity(
                id = 1,
                updatedBy = "Bob"
            )
        }
    }

}

