package com.example.paypast.entity

import com.example.paypast.dto.UserInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "USER_INFORMATION")
data class UserInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val userName: String = "",
    val updateOn: Date = Date(),
    val updatedBy: String = ""
) {
    fun toModel(): UserInfo {
        return UserInfo(userName = userName, updateOn = updateOn)
    }
}

@Repository
interface UserInfoEntityRepository : JpaRepository<UserInfoEntity, Int> {
    fun findByUserName(name: String): UserInfoEntity
    fun findAllByUserName(name: String): List<UserInfoEntity>
}