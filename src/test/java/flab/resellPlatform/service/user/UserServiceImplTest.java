package flab.resellPlatform.service.user;

import flab.utils.UserTestFactory;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.domain.user.UserEntity;
import flab.resellPlatform.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ModelMapper modelMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    RandomValueStringGenerator randomValueStringGenerator;

    @InjectMocks
    UserServiceImpl userServiceImpl;

    @DisplayName("중복된 아이디 존재할 때 실패 검증")
    @Test
    void createUser_idExists() {
        // given
        UserDTO applicantDTO = UserTestFactory.createUserDTOBuilder()
                .username("sameUsername")
                .build();
        UserEntity applicantEntity = UserTestFactory.createUserEntityBuilder()
                .username("sameUsername")
                .build();

        when(userRepository.save(any())).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new SQLIntegrityConstraintViolationException();
            }
        });

        try {
            // when
            userServiceImpl.createUser(applicantDTO);
        } catch (Exception e) {
            // then
            Assertions.assertThat(e).isInstanceOf(SQLIntegrityConstraintViolationException.class);
        }
    }

    @DisplayName("중복된 아이디 존재하지 않을 때 성공 검증")
    @Test
    void createUser_idDontExist() {
        // given
        UserDTO applicantDTO = UserTestFactory.createUserDTOBuilder()
                .username("name1")
                .build();
        UserEntity applicantEntity = UserTestFactory.createUserEntityBuilder()
                .username("name2")
                .build();
        when(userRepository.save(any())).thenReturn(applicantEntity);

        // when
        UserDTO result = userServiceImpl.createUser(applicantDTO);
        // then
        Assertions.assertThat(result).isEqualTo(applicantDTO);
    }
}