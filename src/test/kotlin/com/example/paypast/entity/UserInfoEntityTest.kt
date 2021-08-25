package com.example.paypast.entity

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.text.SimpleDateFormat
import java.util.*


internal class UserInfoEntityTest {

    @Test
    fun toModel() {
        val updatedDate = Date(1546214400000)//"2018-12-31"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val testUserInfo = UserInfoEntity(
            id = 1,
            userName = "jack",
            updatedBy = "admin",
            updateOn = updatedDate

        ).toModel()

        Assertions.assertThat(testUserInfo.userName).isEqualTo("jack")
        Assertions.assertThat(dateFormat.format(testUserInfo.updateOn)).isEqualTo("2018-12-31")
    }
}