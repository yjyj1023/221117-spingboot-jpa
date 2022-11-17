package com.springbootjpa.controller;

import com.springbootjpa.domain.UserResponse;
import com.springbootjpa.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserRestController.class)
class UserRestControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Test
    @DisplayName("입력한 Id로 조회가 잘 되는지")
    void findById() throws Exception {
        given(userService.findById(1l)).willReturn(new UserResponse(1l, "YeonJae", "회원 등록 성공"));
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.message").value("회원 등록 성공"))
                .andDo(print());
    }

    @Test
    @DisplayName("입력한 Id로 조회가 안됐을때 메시지가 잘 출력되는지")
    void findByIdFail() throws Exception {
        given(userService.findById(2l)).willReturn(new UserResponse(null, "", "해당 id의 유저가 없습니다"));
        mockMvc.perform(get("/api/v1/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isEmpty())
                .andExpect(jsonPath("$.message").value("해당 id의 유저가 없습니다"))
                .andDo(print());
    }
}