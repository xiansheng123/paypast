package com.example.paypast.controller

import com.example.paypast.service.UserService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@SpringBootTest
@AutoConfigureMockMvc
internal class LoginControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun authenticateLogin() {
        val content = """
           {
            "userName":"456",
            "password":"12134"
            }
        """
        mvc.perform(
            post("http://localhost:8080/paypass/login")
                .contentType(MediaType.APPLICATION_JSON).content(content)
        )
    }
}