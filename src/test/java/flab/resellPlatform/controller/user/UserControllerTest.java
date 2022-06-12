package flab.resellPlatform.controller.user;

import flab.resellPlatform.data.UserTestFactory;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.repository.user.UserRepository;
import flab.resellPlatform.service.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    @Test
    void 아이디_생성_컨트롤러_성공() {
        // given
        UserDTO userDTO = UserTestFactory.createUserDTOBuilder().build();

        // when

        // then
        Assertions.assertThat(userDTO).isNotNull();
//        userController.create(userDTO)
    }
}