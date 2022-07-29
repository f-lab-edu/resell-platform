package flab.integration_test;

import flab.resellPlatform.ResellPlatformApplication;
import flab.resellPlatform.data.UserTestFactory;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = ResellPlatformApplication.class)
@Transactional
class TestContainersTest extends AbstractDockerComposeBasedTest {

    @Autowired
    RedisTemplate<String, Object> redisCacheTemplate;

    @Autowired
    RedisTemplate<String, Object> redisSessionTemplate;

    @Autowired
    UserService userService;

    @DisplayName("DB 업로드 테스트")
    @Test
    public void testDbUploaded() {
        UserDTO inputUserDTO = UserTestFactory.createUserDTOBuilder().build();
        userService.createUser(inputUserDTO);
        Optional<String> storedUsername = userService.findUsername(inputUserDTO.getPhoneNumber());

        assertThat(inputUserDTO.getUsername()).isEqualTo(storedUsername.get());
    }

    @DisplayName("Redis cache 업로드 테스트")
    @Test
    public void testRedisCacheUploaded() {
        checkRedisOperationWorks(redisCacheTemplate);
    }

    @DisplayName("Redis session 업로드 테스트")
    @Test
    public void testRedisSessionUploaded() {
        checkRedisOperationWorks(redisSessionTemplate);
    }

    void checkRedisOperationWorks(RedisTemplate redisTemplate) {
        String targetKey = "testKey";
        String targetValue = "testValue";
        Object storedValue = redisTemplate.opsForValue().get(targetKey);
        assertThat(storedValue).isNull();

        redisTemplate.opsForValue().set(targetKey, targetValue);
        storedValue = redisTemplate.opsForValue().get(targetKey);
        assertThat(storedValue).isNotNull();
    }
}
