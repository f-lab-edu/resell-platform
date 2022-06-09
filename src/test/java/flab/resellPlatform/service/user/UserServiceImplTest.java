package flab.resellPlatform.service.user;

import flab.resellPlatform.domain.user.UserEntity;
import flab.resellPlatform.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    UserServiceImpl userService;
    @Autowired
    UserRepository userRepository;

    UserEntity.UserEntityBuilder getUserInfoSetUp1 () {
        return UserEntity.builder()
                .username("minsuk")
                .password("a")
                .nickname("uj")
                .email("a@a.com")
                .shoeSize("275")
                .name("ms")
                .phoneNumber("010-3333-1250");
    }

    UserEntity.UserEntityBuilder getUserInfoSetUp2 () {
        return UserEntity.builder()
                .username("hongsuk")
                .password("a")
                .nickname("uj")
                .email("a@a.com")
                .shoeSize("275")
                .name("ms")
                .phoneNumber("010-4499-1212");
    }

    @Test
    void 아이디_중복_yes_검사() {
        // given
        UserEntity existingUser = getUserInfoSetUp1()
                .username("sameUsername")
                .build();
        userRepository.save(existingUser);

        // when
        UserEntity newUser = getUserInfoSetUp2()
                .username("sameUsername")
                .build();
        boolean duplicatedNameExists = userService.checkIfUserNameDuplication(newUser);

        // then
        assertThat(duplicatedNameExists).isTrue();
    }

    @Test
    void 아이디_중복_no_검사() {
        // given
        UserEntity existingUser = UserEntity.builder().username("jack").build();
        userRepository.save(existingUser);

        // when
        UserEntity newUser = UserEntity.builder().username("michael").build();
        boolean duplicatedNameExists = userService.checkIfUserNameDuplication(newUser);

        // then
        assertThat(duplicatedNameExists).isFalse();
    }
}