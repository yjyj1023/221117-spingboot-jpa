package com.springbootjpa.controller;

import com.springbootjpa.domain.UserResponse;
import com.springbootjpa.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserRestController {
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable Long id){
        UserResponse userResponse = userService.findById(id);
        return ResponseEntity.ok().body(userResponse);
    }
}
