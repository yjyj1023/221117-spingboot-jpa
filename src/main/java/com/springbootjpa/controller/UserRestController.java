package com.springbootjpa.controller;

import com.springbootjpa.domain.UserRequest;
import com.springbootjpa.domain.UserResponse;
import com.springbootjpa.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<UserResponse> add(@RequestBody UserRequest userRequest){
        UserResponse userResponse = userService.add(userRequest);
        return ResponseEntity.ok().body(userResponse);
    }
}
