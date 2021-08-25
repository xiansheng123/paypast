package com.example.paypast.service

import com.example.paypast.dto.*
import com.example.paypast.entity.TransactionSummaryEntity
import com.example.paypast.entity.TransactionSummaryRepository
import com.example.paypast.entity.UserMainAccountSummaryEntity
import com.example.paypast.entity.UserMainAccountSummaryRepository
import com.example.paypast.exception.BusinessException
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class AccountService(
    private val userService: UserService,
    private val mainAccountSummaryRepo: UserMainAccountSummaryRepository,
    private val transactionSummaryRepo: TransactionSummaryRepository
) {
    private val ACTION_TOPUP = "Topup"
    private val ACTION_PAY = "Pay"

    fun findAccountByName(name: String): AccountInfo {
        if (userService.isValidUser(name)) {
            throw BusinessException("cannot find this user")
        }
        return mainAccountSummaryRepo.findByUserName(name).toModel()
    }

    fun topUpMoney(topupInfo: TopupInfo): AccountInfo {
        val payerMainAccountInfo = mainAccountSummaryRepo.findByUserName(topupInfo.name)
        // 1 no debt
        if (payerMainAccountInfo.outstandingDebt >= BigDecimal.ZERO) {
            return topupForNoDebtUser(payerMainAccountInfo, topupInfo)
        }
        // 2 has debt
        return topupForDebtUser(payerMainAccountInfo, topupInfo)
    }

    fun payMoney(payInfo: PayInfo): AccountInfo {
        val payerMainAccountInfo = mainAccountSummaryRepo.findByUserName(payInfo.fromName)
        // 1 no debt
        if (payerMainAccountInfo.outstandingDebt >= BigDecimal.ZERO) {
            return payWithoutDebt(payerMainAccountInfo, payInfo)
        }
        // 2 with debt
        if (payerMainAccountInfo.depositsBalance <= BigDecimal.ZERO) {
            throw BusinessException("please topup firstly,thanks")
        }
        return payWithDebt(payerMainAccountInfo, payInfo)
    }

    private fun payWithDebt(payerMainAccountInfo: UserMainAccountSummaryEntity, payInfo: PayInfo): AccountInfo {
        val payerRemaining = payerMainAccountInfo.depositsBalance - payInfo.amount
        val payerUpdatedBalance = if (payerRemaining > BigDecimal.ZERO) payerRemaining else BigDecimal.ZERO
        val payerUpdatedDebt =
            if (payerRemaining > BigDecimal.ZERO) BigDecimal.ZERO else payerRemaining + payerMainAccountInfo.outstandingDebt
        val updatedPayerMainAccountInfo = UserMainAccountSummaryEntity(
            id = payerMainAccountInfo.id,
            userName = payInfo.fromName,
            depositsBalance = payerUpdatedBalance,
            outstandingDebt = payerUpdatedDebt,
            updateOn = Date(),
            updatedBy = payInfo.fromName
        )

        val receiverMainAccountInfo = mainAccountSummaryRepo.findByUserName(payInfo.toName)
        val receiverUpdatedBalance = receiverMainAccountInfo.depositsBalance + payInfo.amount
        val receiverUpdatedDebt = receiverMainAccountInfo.outstandingDebt + payInfo.amount
        val updatedReceiverMainAccountInfo = UserMainAccountSummaryEntity(
            id = payerMainAccountInfo.id,
            userName = payInfo.toName,
            depositsBalance = receiverUpdatedBalance,
            outstandingDebt = receiverUpdatedDebt,
            updateOn = Date(),
            updatedBy = payInfo.fromName
        )

        mainAccountSummaryRepo.save(updatedPayerMainAccountInfo)
        mainAccountSummaryRepo.save(updatedReceiverMainAccountInfo)

        // add transaction to summary
        savePayTransactionSummary(payInfo)
        return updatedPayerMainAccountInfo.toModel()
    }

    private fun payWithoutDebt(
        payerMainAccountInfo: UserMainAccountSummaryEntity,
        payInfo: PayInfo
    ): AccountInfo {
        val payerRemaining = payerMainAccountInfo.depositsBalance - payInfo.amount
        val payerUpdatedBalance = if (payerRemaining > BigDecimal.ZERO) payerRemaining else BigDecimal.ZERO
        val payerUpdatedDebt = if (payerRemaining > BigDecimal.ZERO) BigDecimal.ZERO else payerRemaining
        val updatedPayerMainAccountInfo = UserMainAccountSummaryEntity(
            id = payerMainAccountInfo.id,
            userName = payInfo.fromName,
            depositsBalance = payerUpdatedBalance,
            outstandingDebt = payerUpdatedDebt,
            updateOn = Date(),
            updatedBy = payInfo.fromName
        )

        val receiverMainAccountInfo = mainAccountSummaryRepo.findByUserName(payInfo.toName)
        val receiverUpdatedBalance = receiverMainAccountInfo.depositsBalance + payInfo.amount
        val updatedReceiverMainAccountInfo = UserMainAccountSummaryEntity(
            id = payerMainAccountInfo.id,
            userName = payInfo.toName,
            depositsBalance = receiverUpdatedBalance,
            outstandingDebt = receiverMainAccountInfo.outstandingDebt,
            updateOn = Date(),
            updatedBy = payInfo.fromName
        )

        mainAccountSummaryRepo.save(updatedPayerMainAccountInfo)
        mainAccountSummaryRepo.save(updatedReceiverMainAccountInfo)

        // add transaction to summary
        savePayTransactionSummary(payInfo)
        return updatedPayerMainAccountInfo.toModel()
    }

    private fun savePayTransactionSummary(payInfo: PayInfo) {
        val payerRecord = TransactionSummaryEntity(
            fromUser = payInfo.fromName,
            toUser = payInfo.toName,
            updatedBy = payInfo.fromName,
            action = ACTION_PAY,
            transactionDetail = TransactionDetail(
                totalAmount = payInfo.amount,
                transactionRecordList = listOf(TransactionRecord(payInfo.toName, payInfo.amount))
            ).toJsonString()
        )

        transactionSummaryRepo.save(payerRecord)
    }

    private fun topupForDebtUser(
        payerMainAccountInfo: UserMainAccountSummaryEntity,
        topupInfo: TopupInfo
    ): AccountInfo {
        // update sender and receiver account
        val payerRemainingAmount = payerMainAccountInfo.outstandingDebt + topupInfo.amount
        val isEnoughForDebt = payerRemainingAmount >= BigDecimal.ZERO
        val payerUpdatedBalance = if (isEnoughForDebt) {
            payerMainAccountInfo.depositsBalance + payerRemainingAmount
        } else {
            payerMainAccountInfo.depositsBalance
        }
        val updatedPayerMainAccountInfo = UserMainAccountSummaryEntity(
            id = payerMainAccountInfo.id,
            userName = topupInfo.name,
            depositsBalance = payerUpdatedBalance,
            outstandingDebt = if (isEnoughForDebt) BigDecimal.ZERO else payerRemainingAmount,
            updateOn = Date(),
            updatedBy = topupInfo.name
        )

        val receiverName = payerMainAccountInfo.toModel().debtor
        val receiverMainAccountInfo = mainAccountSummaryRepo.findByUserName(receiverName)
        val receiveAmount = payerMainAccountInfo.outstandingDebt.abs()
        val receiverUpdatedBalance = receiverMainAccountInfo.depositsBalance + receiveAmount
        val receiverOutstandingDebt = receiverMainAccountInfo.outstandingDebt + receiveAmount
        val updatedReceiverMainAccountInfo = UserMainAccountSummaryEntity(
            id = receiverMainAccountInfo.id,
            userName = receiverName,
            depositsBalance = receiverUpdatedBalance,
            outstandingDebt = receiverOutstandingDebt,
            debtor = if (isEnoughForDebt) "" else receiverMainAccountInfo.debtor,
            updateOn = Date(),
            updatedBy = topupInfo.name
        )

        mainAccountSummaryRepo.save(updatedPayerMainAccountInfo)
        mainAccountSummaryRepo.save(updatedReceiverMainAccountInfo)

        // add transaction to summary
        val payerRecord = TransactionSummaryEntity(
            fromUser = topupInfo.name,
            toUser = topupInfo.name,
            updatedBy = topupInfo.name,
            action = ACTION_TOPUP,
            transactionDetail = TransactionDetail(
                totalAmount = topupInfo.amount,
                transactionRecordList = if (isEnoughForDebt) {
                    listOf(TransactionRecord(topupInfo.name, payerRemainingAmount))
                    listOf(TransactionRecord(receiverName, receiveAmount))
                } else {
                    listOf(TransactionRecord(receiverName, topupInfo.amount))
                }
            ).toJsonString()
        )

        transactionSummaryRepo.save(payerRecord)
        return updatedPayerMainAccountInfo.toModel()
    }

    private fun topupForNoDebtUser(
        payerMainAccountInfo: UserMainAccountSummaryEntity,
        topupInfo: TopupInfo
    ): AccountInfo {
        // update sender
        val updatedBalance = payerMainAccountInfo.depositsBalance + topupInfo.amount
        val updatedAccountInfo = payerMainAccountInfo.copy(
            depositsBalance = updatedBalance,
            updateOn = Date(),
            userName = topupInfo.name
        )
        mainAccountSummaryRepo.save(updatedAccountInfo)

        // add transaction to summary
        val addedTransaction = TransactionSummaryEntity(
            fromUser = topupInfo.name,
            toUser = topupInfo.name,
            updatedBy = topupInfo.name,
            action = ACTION_TOPUP
        )
        transactionSummaryRepo.save(addedTransaction)
        return updatedAccountInfo.toModel()
    }
}
