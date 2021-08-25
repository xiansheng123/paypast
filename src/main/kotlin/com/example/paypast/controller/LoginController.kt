package com.example.paypast.controller

import com.example.paypast.dto.LoginInfo
import com.example.paypast.exception.ValidationException
import com.example.paypast.service.UserService
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse


@RestController
class LoginController(private val userService: UserService) {

    /*
    * By right we could verify user by token in header of request
    * But Currently we use sample encryption to implement it
    * */
    @PostMapping("/login")
    fun authenticateLogin(@RequestBody loginInfo: LoginInfo, response: HttpServletResponse): String {
        if (loginInfo.userName.isBlank() || loginInfo.password.isBlank()) {
            throw ValidationException("username:${loginInfo.userName}")
        }
        return "ok"
    }

    @GetMapping("/username/{name}")
    fun getUserByName(@PathVariable("name") name: String) = userService.findUserByName(name)

    @GetMapping("/username/all")
    fun getAllUser() = userService.findAllUsers()

    @PostMapping("/add")
    fun adduser(@RequestBody loginInfo: LoginInfo) = userService.createUser(loginInfo)

}