package flab.resellPlatform.service.user;

import flab.resellPlatform.data.UserTestFactory;
import flab.resellPlatform.domain.user.UserEntity;
import flab.resellPlatform.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    UserServiceImpl userServiceImpl;

    @Test
    void 아이디_중복_yes_검사() {
        // given
        UserEntity applicant = UserTestFactory.createUserEntityBuilder()
                .username("sameUsername")
                .build();
        when(userRepository.getUsernameCount(applicant.getUsername())).thenReturn(1);

        // when
        boolean duplicatedNameExists = userServiceImpl.checkIfUserNameDuplication(applicant);

        // then
        assertThat(duplicatedNameExists).isTrue();
    }

    @DisplayName("아이디 중복 검사")
    void 아이디_중복_no_검사() {
        // given
        UserEntity applicant = UserTestFactory.createUserEntityBuilder()
                .username("michael")
                .build();
        when(userRepository.getUsernameCount(applicant.getUsername())).thenReturn(0);

        // when
        boolean duplicatedNameExists = userServiceImpl.checkIfUserNameDuplication(applicant);

        // then
        assertThat(duplicatedNameExists).isFalse();
    }
}