package flab.integrationtest;

import flab.resellPlatform.ResellPlatformApplication;
import flab.utils.UserTestFactory;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.service.user.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = ResellPlatformApplication.class)
class TestContainersTest extends AbstractDockerComposeBasedTest {

    @Autowired
    RedisTemplate<String, Object> redisCacheTemplate;

    @Autowired
    RedisTemplate<String, Object> redisSessionTemplate;

    @Autowired
    UserService userService;

    @AfterEach
    void setup() {
        redisCacheTemplate.delete(redisCacheTemplate.keys("*"));
        redisSessionTemplate.delete(redisSessionTemplate.keys("*"));
    }

    @DisplayName("DB 업로드 테스트")
    @Test
    @Transactional
    public void testDbUploaded() {
        UserDTO inputUserDTO = UserTestFactory.createUserDTOBuilder().build();
        userService.createUser(inputUserDTO);
        String storedUsername = userService.findUsername(inputUserDTO.getPhoneNumber());

        assertThat(inputUserDTO.getUsername()).isEqualTo(storedUsername);
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
