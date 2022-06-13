package flab.resellPlatform.repository.user;

import flab.resellPlatform.data.UserTestFactory;
import flab.resellPlatform.domain.user.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MybatisUserRepositoryTest {

    @Mock
    UserMapper userMapper;

    @InjectMocks
    MybatisUserRepository mybatisUserRepository;

    @Test
    void 유저_정보_저장() {
        UserEntity userEntity = UserTestFactory.createUserEntityBuilder().build();
        mybatisUserRepository.save(userEntity);
        verify(userMapper, times(1)).save(userEntity);
    }

    @Test
    void 유저_정보_전부_조회() {
        // given
        UserEntity userEntity1 = UserTestFactory.createUserEntityBuilder()
                .username("min")
                .build();
        UserEntity userEntity2 = UserTestFactory.createUserEntityBuilder()
                .username("hong")
                .build();
        UserEntity userEntity3 = UserTestFactory.createUserEntityBuilder()
                .username("kimjung")
                .build();
        when(userMapper.findAll()).thenReturn(Arrays.asList(userEntity1, userEntity2, userEntity3));

        // when
        List<UserEntity> foundUsers =  mybatisUserRepository.findAll();

        // then
        verify(userMapper, times(1)).findAll();
        assertThat(foundUsers).contains(userEntity1, userEntity2, userEntity3);
        assertThat(foundUsers.size()).isEqualTo(3);
    }
}