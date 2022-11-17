package com.springbootjpa.service;

import com.springbootjpa.domain.User;
import com.springbootjpa.domain.UserRepository;
import com.springbootjpa.domain.UserRequest;
import com.springbootjpa.domain.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class UserServiceTest {
    private UserRepository userRepository = Mockito.mock(UserRepository.class); //Mockito를 통해 Repository를 DI한다.

    private UserService userService;

    @BeforeEach
    void setUp(){
        userService = new UserService(userRepository); //SpringBoot를 사용하지 않고 수동으로 DI한다.
    }

    @Test
    @DisplayName("회원 등록 메시지가 나오는지")
    void addUser() {
        Mockito.when(userRepository.save(any()))
                .thenReturn(new User(1l, "YeonJae", "1234"));

        UserResponse userResponse = userService.add(new UserRequest("YeonJae","1234"));
        assertEquals("YeonJae", userResponse.getUsername());
        assertEquals("유저 등록이 성공했습니다.", userResponse.getMessage());
    }
}