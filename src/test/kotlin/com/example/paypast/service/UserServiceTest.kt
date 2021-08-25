package com.example.paypast.service

import com.example.paypast.entity.UserInfoEntity
import com.example.paypast.entity.UserInfoEntityRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class UserServiceTest {
    @MockK
    lateinit var userInfoEntityRepo: UserInfoEntityRepository

    @InjectMockKs
    lateinit var UserService: UserService

    @Test
    fun isValidUser_Invalid() {
        every { userInfoEntityRepo.findAllByUserName(any()) } returns
                emptyList()
        val result = UserService.isValidUser("admin")
        Assertions.assertThat(result).isFalse
    }

    @Test
    fun isValidUser_Valid() {
        every { userInfoEntityRepo.findAllByUserName("admin") } returns
                listOf(UserInfoEntity(1, "admin", Date(), "admin"))
        val result = UserService.isValidUser("admin")
        Assertions.assertThat(result).isTrue
    }
}


