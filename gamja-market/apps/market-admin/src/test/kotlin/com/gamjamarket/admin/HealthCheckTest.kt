package com.gamjamarket.admin

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class HealthCheckTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun healthCheck() {
        mockMvc.perform(get("/actuator/health"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("UP"))
    }
}