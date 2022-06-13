package flab.resellPlatform.controller.user;

import flab.resellPlatform.domain.user.LoginInfo;
import flab.resellPlatform.service.user.LoginService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginService loginService;

    @Test
    void 유저_데이터_있음_로그인_테스트() throws Exception {
        // given
        LoginInfo loginInfo = new LoginInfo("minsuk", "123");
        when(loginService.doLogin(any())).thenReturn(Optional.of(loginInfo));

        // when
        String loginData = "username=minsuk&password=123";
        ResultActions resultActions = mockMvc.perform(post("/login")
                .content(loginData)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_FORM_URLENCODED));
        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 유저_데이터_없음_로그인_테스트() throws Exception {
        // given
        when(loginService.doLogin(any())).thenReturn(Optional.empty());

        // when
        String loginData = "username=minsuk&password=123";
        ResultActions resultActions = mockMvc.perform(post("/login")
                .content(loginData)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_FORM_URLENCODED));
        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}