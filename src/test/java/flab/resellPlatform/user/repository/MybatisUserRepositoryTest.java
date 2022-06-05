package flab.resellPlatform.user.repository;

import flab.resellPlatform.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MybatisUserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void save() {
        // given
        User user = new User("michael", "123", "010-4589-3250", "minsuk", "uj", "alstjrdl852@naver.com", "275");

        // when
        userRepository.save(user);

        // then
        User foundUser = userRepository.findById(user.getId()).get();
        assertThat(user).isEqualTo(foundUser);
    }

    @Test
    void findAll() {
        // given
        User user1 = new User("michael", "123", "010-4589-1250", "minsuk", "uj", "alstjrdl852@naver.com", "275");
        User user2 = new User("michael", "123", "010-4589-2250", "minsuk", "uj", "alstjrdl852@naver.com", "275");
        User user3 = new User("michael", "123", "010-4589-3250", "minsuk", "uj", "alstjrdl852@naver.com", "275");
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        // when
        List<User> users = userRepository.findAll();

        // then
        assertThat(users).contains(user1, user2, user3);
    }
}