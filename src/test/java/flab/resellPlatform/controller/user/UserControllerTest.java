package flab.resellPlatform.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import flab.resellPlatform.MessageConfig;
import flab.resellPlatform.controller.response.DefaultResponse;
import flab.resellPlatform.data.UserTestFactory;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, MessageConfig.class})
class UserControllerTest {

    @Autowired
    MessageSourceAccessor messageSourceAccessor;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @DisplayName("아이디 생성 성공")
    @Test
    void create_success() throws Exception {
        // given
        UserDTO userDTO = UserTestFactory.createUserDTOBuilder().build();
        when(userService.join(any())).thenReturn(Optional.of(userDTO));
        ObjectMapper mapper = new ObjectMapper();
        String userData = mapper.writeValueAsString(userDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/users/join")
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
        ResultActions resultActions = mockMvc.perform(post("/users/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userData));
        // then
        Map<String, Object> returnObjects = Map.of();
        DefaultResponse defaultResponse = DefaultResponse.builder()
                .messageSummary(messageSourceAccessor.getMessage("common.argument.invalid"))
                .data(returnObjects)
                .build();

        expectDefaultResponse(mapper, defaultResponse, resultActions);
    }

    @DisplayName("아이디 생성 실패 by 아이디 중복")
    @Test
    void create_failByIdDuplication() throws Exception {
        // given
        UserDTO userDTO = UserTestFactory.createUserDTOBuilder().build();
        when(userService.join(any())).thenReturn(Optional.empty());
        ObjectMapper mapper = new ObjectMapper();
        String userData = mapper.writeValueAsString(userDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/users/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userData));

        // then
        Map<String, Object> returnObjects = Map.of();
        DefaultResponse defaultResponse = DefaultResponse.builder()
                .messageSummary(messageSourceAccessor.getMessage("user.username.duplication"))
                .data(returnObjects)
                .build();
        expectDefaultResponse(mapper, defaultResponse, resultActions);
    }

    private void expectDefaultResponse(ObjectMapper mapper, DefaultResponse defaultResponse, ResultActions resultActions) throws Exception {
        String defaultResponseJson = mapper.writeValueAsString(defaultResponse);
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(content().string(defaultResponseJson));
    }
}
