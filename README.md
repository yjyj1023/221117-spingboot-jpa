# 221117-spingboot-jpa

# 1. 지난 시간 복습
- 지난시간 진행했던 **프로젝트의 구조**이다.
![](https://velog.velcdn.com/images/lyj1023/post/0d5028cf-b975-46d4-89cb-aa55b52755bb/image.png)
- 먼저 왼쪽은 클래스의 의존성을 나타내고 오른쪽은 테스트 코드의 의존성을 나타낸다.
- 지난시간에는 컨트롤러의 테스트만 진행했었는데 컨트롤러는 테스트하기 어려웠었다.
- 왜냐하면 그림의 왼쪽처럼 컨트롤러가 서비스와 레포지토리를 전부 의존하고 있기때문에 컨트롤러 테스트를 진행하려면 서비스와 레포지토리를 전부 불러와야 하기 때문에 Unit Test(Controller, Service, Repository를 각각 테스트 하는것)가 성립하지 않기 때문이다.
- 따라서 테스트를 실행하는 시간이 길어지고 DB에도 의존하게 된다.
- 이것은 테스트의 원칙에 어긋나기 때문에 특정부분만 테스트 할 수 있도록 가짜 객체를 만들어서 컨트롤러, 서비스, 레포지토리 각각을 테스트 할 수 있도록 만든다.
- 위에서 말했던 가짜 객체의 역할을 하는 것이 Mock이다.
- Mock는 예를들어 service.findById()라는 메소드가 있다고 할때 이 메소드가 실행 되었다고 가정하고 그 return 값을 넣어준다.

- TDD는 어렵기 때문에 원래는 Test를 만들어 통과하고 기능을 만들어야 하지만 임시 방편으로 Test를 만들기 전에 기능을 만들고 Test를 만드는 연습을 해보자.

# 2. 학습 목표
- 지난시간에 Article로 했던 프로젝트를 User로 바꿔서 다시한번 연습해본다.
- Test 코드를 짜고 기능을 만드는 연습이 아직 덜됐기 때문에 먼저 기능을 만들고 나중에 테스트 코드를 붙이도록 한다.

- __파일 구조__

![](https://velog.velcdn.com/images/lyj1023/post/f9aefaae-1592-4b54-badd-e1b7b15eb0bd/image.png)

- __의존성 추가__

![](https://velog.velcdn.com/images/lyj1023/post/b641a03f-0c71-4e80-81ec-6ef2b375c405/image.png)

# 3. 기본 틀 만들기
- json형식으로 {"id", "username", "massage"} 을 리턴하는 GET /api/v1/users 를 만들어보자.

## 1) Entity 클래스, Repository 생성
- Entity 클래스와 Repository를 만든다.

`[User.java]`
```java
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
```

`[UserRepository.java]`
```java
package com.springbootjpa.domain;

import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User,Long> {}
```

## 2) UserRequest 클래스, UserResponse 클래스 생성
- 요청을 받는 UserRequest 클래스와 반환을 해주는 UserResponse 클래스를 만든다.
- UserResponse 클래스는 유저에게 반환해주는 값으로 id, username, message가 들어간다.

`[UserRequest.java]`
```java
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
}
```

`[UserResponse.java]`
```java
package com.springbootjpa.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String message;
}
```

## 3) Service 클래스 생성
- Service는 Repository 인터페이스를 사용하는 클래스로 findById 메소드는 id를 찾아서 값이 없으면 UserResponse 형식으로 `{id, "", "해당 id의 유저가 없습니다."}`를 반환하고 값이 있으면 UserResponse 형식으로 `{user.getId(), user.getUsername(), ""}` 를 반환한다.

`[UserService.java]`
```java
package com.springbootjpa.service;

import com.springbootjpa.domain.User;
import com.springbootjpa.domain.UserRepository;
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
}
```

## 4) UserRestController 생성
- get 메소드는 id를 값을 받아서 service의 findById 메소드를 사용해 UserResponse를 반환한다.

`[UserRestController.java]`
```java
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
```

## 5) 결과 확인하기
- 아직 DB에 add 해주는 메소드가 없기 때문에 수동으로 DB에 값을 넣는다.

![](https://velog.velcdn.com/images/lyj1023/post/ce260df8-10b0-401d-8f90-7f810a901527/image.png)

- http://localhost:8080/api/v1/users/1 로 들어가보면 JSON 형식으로 잘 출력되는것을 확인 할 수 있다.

![](https://velog.velcdn.com/images/lyj1023/post/32329329-631d-41b6-af2b-845bd8419c71/image.png)

- http://localhost:8080/api/v1/users/2 으로 들어가면 DB에 id가 2인 값이 없기 때문에 아래와 같이 출력된다.

![](https://velog.velcdn.com/images/lyj1023/post/cdfbaeb1-e7d2-4f5e-9588-421ae9e04a2a/image.png)

# 4. User add 기능 추가
- Service에 add 기능을 추가한다.

## 1) Entity 클래스 수정
- User에 @Builder와 id를 자동으로 생성하기 위해 @GeneratedValue 어노테이션을 추가한다.
- Id를 수정하고 DB를 다시 만들어야 id가 자동으로 생성된다.

`[User.java]`
```java
package com.springbootjpa.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String username;
    String password;
}
```

## 2) UserRequest에 toEntity 추가
- 요청받은 값을 Entity 형식으로 바꿔주는 메소드를 추가한다.

`[UserRequest.java]`
```java
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
```

## 3) UserService에 add 메소드 추가
- Service에 add기능을 하는 메소드를 추가한다.

`[UserService.java]`
```java
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
```

## 4) UserRestController에 add 메소드 추가
- Controller에 UserRequest를 받아서 UserService의 add 메소드를 사용해 UserResponse 형식으로 리턴해주는 메소드를 추가한다.
- JSON형식으로 값을 받을것이기 때문에 매개변수에 @RequestBody 어노테이션을 꼭 추가해준다.

`[UserRestController.java]`
```java
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
```

## 5) 결과 확인하기
- body에 `{"username":"YeonJae", "password":"1234"}`를 넣으면 성공적으로 유저가 등록이 된 것을 확인할 수 있다.

![](https://velog.velcdn.com/images/lyj1023/post/7b035563-e942-4294-bdf9-529e4e6afa44/image.png)

# 5. 중복 체크 기능 추가
- username이 중복되는 경우 다른 이름으로 변경하라는 메세지가 출력되는 기능을 추가한다.

## 1) UserRepository 추가
- UserRepository에 username을 찾는 기능을 추가한다.

`[UserRepository.java]`
```java
package com.springbootjpa.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
}
```

## 2) UserService의 add 메소드 수정
- 기존에 아무 username이나 전부 추가하던 add 메소드를 request받은 값에서 username을 찾아 이름이 없으면 DB에 user를 추가하고, 있으면 DB에 저장하지 않고 "이 user 는 이미 존재 합니다. 다른 이름을 사용하세요." 메세지를 출력하도록 수정한다.

`[UserService.java]`
```java
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
        Optional<User> optUser = userRepository.findByUsername(user.getUsername());
        if(optUser.isEmpty()){
            User savedUser = userRepository.save(user);
            return new UserResponse(savedUser.getId(), savedUser.getUsername(), "유저 등록이 성공했습니다.");
        }else {
            User user2 = optUser.get();
            return new UserResponse(null, user2 .getUsername(), "이 user 는 이미 존재 합니다. 다른 이름을 사용하세요.");
        }
    }
}
```

## 3) 결과 확인하기
- 위에서 추가한 user와 똑같이 body에 입력하면 아래와 같이 메세지가 출력되며 DB에 저장되지 않는다.

![](https://velog.velcdn.com/images/lyj1023/post/06e51851-7e96-4472-bf10-c8ce119e0b2d/image.png)

![](https://velog.velcdn.com/images/lyj1023/post/c97eca22-121a-43ad-97c5-e7107e86ad5f/image.png)

- 중복되지 않는 경우 아래처럼 DB에 잘 저장된다.

![](https://velog.velcdn.com/images/lyj1023/post/a261d951-4819-4db8-97b4-300e6516a1a1/image.png)

![](https://velog.velcdn.com/images/lyj1023/post/42b93c3c-ff96-4f6f-a613-093310bad1e9/image.png)

# 6. 테스트 만들기
- 테스트는 원래 controller, service, repository 3개를 만들어야 하지만 repository는 Jpa에서 이미 검증된 것들을 가져다 쓰기 때문에 제외하고 controller와 service만 테스트 해보자.

## 1) Controller 테스트 생성
- 먼저 Controller를 테스트해보자.
- findById 메소드는 id를 추가했을때 그 값이 잘 출력이 되는지 확인하는 테스트이고 findByIdFail 메소드는 id를 추가했을때 그 값이 없는지 잘 확인하는 테스트이다.

`[UserRestControllerTest.java]`
```java
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
```

## 2) Service 테스트 생성
- 어노테이션 없이 테스트 할 수 있으면 가장 좋은데 그 이유는 속도가 빠르기 때문이다. (ApplicationContext에 Bean을 덜 업로드 해도 되기 때문에)
- 또한 Service는 Pojo(Plain Old Java Object)로 만드는 것이 좋다.

`[UserServiceTest.java]`
```java
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
```
