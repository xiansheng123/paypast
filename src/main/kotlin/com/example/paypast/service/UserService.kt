package com.example.paypast.service

import com.example.paypast.dto.LoginInfo
import com.example.paypast.dto.UserInfo
import com.example.paypast.entity.UserInfoEntity
import com.example.paypast.entity.UserInfoEntityRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userInfoEntityRepo: UserInfoEntityRepository) {

    fun findUserByName(name: String): UserInfoEntity {
        return userInfoEntityRepo.findByUserName(name)
    }

    fun findAllUsers(): List<UserInfoEntity> {
        return userInfoEntityRepo.findAll()
    }

    fun isValidUser(name: String): Boolean {
        if (name.isBlank()) {
            return false
        }

        val list = userInfoEntityRepo.findAllByUserName(name)
        if (list.isEmpty()) {
            return false
        }
        return true
    }

    fun createUser(loginInfo: LoginInfo): UserInfo {
        val user = userInfoEntityRepo.save(
            UserInfoEntity(userName = loginInfo.userName, updatedBy = "admin")
        )
        return user.toModel()
    }
}