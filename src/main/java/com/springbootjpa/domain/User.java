package com.springbootjpa.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class User {
    @Id
    Long id;
    String username;
    String password;

}
