package flab.resellPlatform.controller.user;

import flab.resellPlatform.common.SessionConst;
import flab.resellPlatform.domain.user.LoginInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.filter.DelegatingFilterProxy;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    MockMvc mockMvc;

    LoginInfo getLoginInfoSetup () {
        return new LoginInfo("minsuk", "123");
    }

    @DisplayName("로그인된 채 홈 진입")
    @Test
    @WithMockUser
    void getHome_with_login() throws Exception {
        // given
        LoginInfo loginInfo = getLoginInfoSetup();

        // when
        ResultActions resultActions2 = mockMvc.perform(get("/")
                .sessionAttr(SessionConst.LOGIN_INFO, loginInfo));

        // then
        resultActions2.andExpect(status().isOk());
    }

    @DisplayName("로그인_안된_채_홈_진입")
    @Test
    void getHome_without_login() throws Exception {
        // 세션 키 자체가 없는 경우
        // when
        ResultActions resultActions1 = mockMvc.perform(get("/"));
        // then
        resultActions1.andExpect(status().isUnauthorized());

        // 세션 키는 있지만 로그인 용 세션 키가 아닌 경우
        // given
        LoginInfo loginInfo = getLoginInfoSetup();
        // when
        ResultActions resultActions2 = mockMvc.perform(get("/")
                .requestAttr("not_login_info", loginInfo));
        // then
        resultActions2.andExpect(status().isUnauthorized());
    }
}
