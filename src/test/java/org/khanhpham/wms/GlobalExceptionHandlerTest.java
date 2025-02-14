package org.khanhpham.whs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

//@WebMvcTest(TestController.class)
class GlobalExceptionHandlerTest {
    private final MockMvc mockMvc;

    GlobalExceptionHandlerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void testGlobalExceptionHandler() throws Exception {
        mockMvc.perform(get("/api/error")) // Gọi API lỗi
                .andExpect(status().isInternalServerError()) // Kiểm tra HTTP 500
                .andExpect(jsonPath("$.timestamp").exists()) // Kiểm tra có timestamp
                .andExpect(jsonPath("$.message").value("Đây là lỗi thử nghiệm!")) // Kiểm tra message
                .andExpect(jsonPath("$.details").value(containsString("/api/error"))); // Kiểm tra URL lỗi
    }
}
