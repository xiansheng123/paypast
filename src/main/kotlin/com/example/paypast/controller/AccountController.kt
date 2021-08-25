package com.example.paypast.controller

import com.example.paypast.dto.PayInfo
import com.example.paypast.dto.TopupInfo
import com.example.paypast.dto.UserInfo
import com.example.paypast.service.AccountService
import com.example.paypast.service.UserService
import org.springframework.web.bind.annotation.*


@RestController
class AccountController(
    private val userService: UserService,
    private val accountService: AccountService
) {

    @GetMapping("/account/{name}")
    fun getUserByName(@PathVariable("name") name: String): UserInfo {
        return userService.findUserByName(name).toModel()
    }

    @PostMapping("topup")
    fun recharge(@RequestBody topupInfo: TopupInfo) = accountService.topUpMoney(topupInfo)

    @PostMapping("pay")
    fun recharge(@RequestBody payInfo: PayInfo) = accountService.payMoney(payInfo)
}