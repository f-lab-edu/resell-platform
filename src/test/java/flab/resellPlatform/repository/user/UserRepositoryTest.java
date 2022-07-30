package flab.resellPlatform.repository.user;

import flab.resellPlatform.data.UserTestFactory;
import flab.resellPlatform.domain.user.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MybatisUserRepository.class})
@Import(value = {UserRepositoryTest.CachingTestConfig.class})
class UserRepositoryTest {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final String CACHE_NAME = "user";

    @MockBean
    UserMapper userMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CacheManager cacheManager;

    UserEntity userEntity;

    @BeforeEach
    void setUp() {
        cacheManager.getCache(CACHE_NAME).clear();
        userEntity = UserTestFactory.createUserEntityBuilder().build();
    }

    @DisplayName("findUser cache hit")
    @Test
    void findUser_cache_hit() throws Exception {
        // given
        when(userMapper.findByUsername(userEntity.getUsername())).thenReturn(Optional.of(userEntity));
        userRepository.findUser(userEntity.getUsername());
        verify(userMapper, times(1)).findByUsername(userEntity.getUsername());

        // when
        userRepository.findUser(userEntity.getUsername());

        // then
        Assertions.assertThat(cacheManager).isNotNull();
        verifyNoMoreInteractions(userMapper);
    }

    @DisplayName("findUser cache miss")
    @Test
    void findUser_cache_miss() {
        // given
        when(userMapper.findByUsername(userEntity.getUsername())).thenReturn(Optional.of(userEntity));

        // when
        userRepository.findUser(userEntity.getUsername());

        // then
        verify(userMapper, times(1)).findByUsername(userEntity.getUsername());
    }

    @Configuration
    @EnableCaching
    public static class CachingTestConfig {
        @Bean
        CacheManager cacheManager() {
            CacheManager cacheManager = new ConcurrentMapCacheManager(CACHE_NAME);
            return cacheManager;
        }
    }
}