package com.springbootjpa.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    String username;
    String password;

    public User toEntity(){
        User user = User.builder()
                .username(this.username)
                .password(this.password)
                .build();

        return user;
    }
}
