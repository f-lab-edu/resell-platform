package flab.resellPlatform.repository.user;

import flab.resellPlatform.domain.user.UserEntity;
import org.apache.catalina.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class MybatisUserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    public UserEntity userInfoSetUp() {
        return UserEntity.builder()
                .username("michael")
                .password("123")
                .phoneNumber("010-4589-1250")
                .name("minsuk")
                .nickname("uj")
                .email("alstjrdl852@naver.com")
                .shoeSize("275")
                .build();
    }

    @DisplayName("repository save 검사 by phoneNumber")
    @Test
    void save() {
        // given
        UserEntity userEntity = userInfoSetUp();
        // when
        userRepository.save(userEntity);
        // then
        UserEntity foundUserEntity = userRepository.findByPhoneNumber(userEntity.getPhoneNumber()).get();
        assertThat(userEntity).isEqualTo(foundUserEntity);
    }

    @Test
    void findAll() {
        // given
        UserEntity userEntity1 = new UserEntity("michael", "123", "010-4589-1250", "minsuk", "uj", "alstjrdl852@naver.com", "275");
        UserEntity userEntity2 = new UserEntity("michael", "123", "010-4589-2250", "minsuk", "uj", "alstjrdl852@naver.com", "275");
        UserEntity userEntity3 = new UserEntity("michael", "123", "010-4589-3250", "minsuk", "uj", "alstjrdl852@naver.com", "275");
        userRepository.save(userEntity1);
        userRepository.save(userEntity2);
        userRepository.save(userEntity3);

        // when
        List<UserEntity> userEntities = userRepository.findAll();

        // then
        assertThat(userEntities).contains(userEntity1, userEntity2, userEntity3);
    }
}