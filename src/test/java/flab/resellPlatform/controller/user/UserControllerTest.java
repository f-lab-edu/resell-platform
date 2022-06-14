package flab.resellPlatform.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import flab.resellPlatform.common.ExceptionAdvice;
import flab.resellPlatform.controller.response.DefaultResponse;
import flab.resellPlatform.data.UserTestFactory;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.service.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.validation.constraints.Email;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @InjectMocks
    UserController userController;

    @DisplayName("아이디 생성 성공")
    @Test
    void create_success() throws Exception {
        // given
        UserDTO userDTO = UserTestFactory.createUserDTOBuilder().build();
        when(userService.join(any())).thenReturn(Optional.of(userDTO));
        ObjectMapper mapper = new ObjectMapper();
        String userData = mapper.writeValueAsString(userDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userData));
        // then
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("아이디 생성 실패 by 유효성 검사")
    @Test
    void create_failByValidation() throws Exception {
        // given
        UserDTO userDTO = UserTestFactory.createUserDTOBuilder()
                .email("alstjrdl852naver.com")
                .build();
        ObjectMapper mapper = new ObjectMapper();
        String userData = mapper.writeValueAsString(userDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userData));
        // then
        DefaultResponse defaultResponse = getDefaultResponse(
                ExceptionAdvice.ErrorMessage.INVALID_INPUT,
                userDTO, "email",
                UserDTO.errorMessage.EMAIL_FORM_ERROR);
        expectDefaultResponse(mapper, defaultResponse, resultActions);
    }

    @DisplayName("아이디 생성 실패 by 아이디 중복")
    @Test
    void create_failByIdDuplication() throws Exception {
        /// given
        UserDTO userDTO = UserTestFactory.createUserDTOBuilder().build();
        when(userService.join(any())).thenReturn(Optional.empty());
        ObjectMapper mapper = new ObjectMapper();
        String userData = mapper.writeValueAsString(userDTO);
        System.out.println(userDTO);
        System.out.println(userData);

        // when
        ResultActions resultActions = mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userData));

        // then
        DefaultResponse defaultResponse = getDefaultResponse(
                UserController.ErrorMessage.NEED_UNIQUE_VALUE,
                userDTO, "username",
                UserDTO.errorMessage.USERNAME_DUPLICATION);
        expectDefaultResponse(mapper, defaultResponse, resultActions);
    }

    private void expectDefaultResponse(ObjectMapper mapper, DefaultResponse defaultResponse, ResultActions resultActions) throws Exception {
        String defaultResponseJson = mapper.writeValueAsString(defaultResponse);
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().string(defaultResponseJson));
    }

    private DefaultResponse getDefaultResponse(String errorMessageSummery, UserDTO userDTO, String email, String emailFormError) {
        DefaultResponse defaultResponse = DefaultResponse.builder()
                .messageSummary(errorMessageSummery)
                .requestDTO(userDTO)
                .errorMessages(Map.of(email, emailFormError))
                .build();
        return defaultResponse;
    }
}
