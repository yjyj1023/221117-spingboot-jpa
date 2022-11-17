package com.springbootjpa.service;

import com.springbootjpa.domain.User;
import com.springbootjpa.domain.UserRepository;
import com.springbootjpa.domain.UserRequest;
import com.springbootjpa.domain.UserResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse findById(Long id){
        Optional<User> optUser = userRepository.findById(id);
        if(optUser.isEmpty()) {
            return new UserResponse(id, "", "해당 id의 유저가 없습니다.");
        }else{
            User user = optUser.get();
            return new UserResponse(user.getId(), user.getUsername(), "");
        }
    }

    public UserResponse add(UserRequest userRequest){
        User user = userRequest.toEntity();
        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser.getId(), savedUser.getUsername(), "유저 등록이 성공했습니다.");
    }
}
