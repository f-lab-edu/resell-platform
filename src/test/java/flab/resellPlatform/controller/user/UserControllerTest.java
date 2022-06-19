package flab.resellPlatform.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import flab.resellPlatform.MessageConfig;
import flab.resellPlatform.controller.response.StandardResponse;
import flab.resellPlatform.data.UserTestFactory;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, MessageConfig.class})
@WithMockUser
class UserControllerTest {

    @Autowired
    MessageSourceAccessor messageSourceAccessor;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @DisplayName("아이디 생성 성공")
    @Test
    void createUser_success() throws Exception {
        // given
        UserDTO userDTO = UserTestFactory.createUserDTOBuilder().build();
        when(userService.createUser(any())).thenReturn(Optional.of(userDTO));
        ObjectMapper mapper = new ObjectMapper();
        String userData = mapper.writeValueAsString(userDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userData)
                .with(csrf()));
        // then
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("아이디 생성 실패 by 유효성 검사")
    @Test
    void createUser_failByValidation() throws Exception {
        // given
        UserDTO userDTO = UserTestFactory.createUserDTOBuilder()
                .email("alstjrdl852naver.com")
                .build();
        ObjectMapper mapper = new ObjectMapper();
        String userData = mapper.writeValueAsString(userDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userData)
                .with(csrf()));
        // then
        Map<String, Object> returnObjects = Map.of();
        StandardResponse defaultResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("common.argument.invalid"))
                .data(returnObjects)
                .build();

        expectDefaultResponse(mapper, defaultResponse, resultActions);
    }

    @DisplayName("아이디 생성 실패 by 아이디 중복")
    @Test
    void createUser_failByIdDuplication() throws Exception {
        // given
        UserDTO userDTO = UserTestFactory.createUserDTOBuilder().build();
        when(userService.createUser(any())).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new SQLIntegrityConstraintViolationException();
            }
        });
        ObjectMapper mapper = new ObjectMapper();
        String userData = mapper.writeValueAsString(userDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userData)
                .with(csrf()));

        // then
        Map<String, Object> returnObjects = Map.of();
        StandardResponse defaultResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.username.duplicated"))
                .data(returnObjects)
                .build();
        expectDefaultResponse(mapper, defaultResponse, resultActions);
    }

    private void expectDefaultResponse(ObjectMapper mapper, StandardResponse standardResponse, ResultActions resultActions) throws Exception {
        String defaultResponseJson = mapper.writeValueAsString(standardResponse);
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().string(defaultResponseJson));
    }
}
