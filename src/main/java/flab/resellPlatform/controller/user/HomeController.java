package flab.resellPlatform.controller.user;

import flab.resellPlatform.common.SessionConst;
import flab.resellPlatform.controller.response.DefaultResponse;
import flab.resellPlatform.domain.user.LoginInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final MessageSourceAccessor messageSourceAccessor;

    @GetMapping
    public ResponseEntity getHome(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);

        DefaultResponse.DefaultResponseBuilder defaultResponseBuilder = DefaultResponse.builder()
                .data(Map.of());

        if (session == null) {
            return getDefaultResponseResponseEntity(defaultResponseBuilder, "common.login.need", HttpStatus.UNAUTHORIZED);
        }

        LoginInfo loginInfo = (LoginInfo) session.getAttribute(SessionConst.LOGIN_INFO);
        if (loginInfo == null) {
            return getDefaultResponseResponseEntity(defaultResponseBuilder, "common.login.need", HttpStatus.UNAUTHORIZED);
        }

        return getDefaultResponseResponseEntity(defaultResponseBuilder, "home.welcome", HttpStatus.OK);
    }

    private ResponseEntity<DefaultResponse> getDefaultResponseResponseEntity(DefaultResponse.DefaultResponseBuilder defaultResponseBuilder, String messageCode, HttpStatus httpStatus) {
        DefaultResponse defaultResponse = defaultResponseBuilder
                .messageSummary(messageSourceAccessor.getMessage(messageCode))
                .build();

        return ResponseEntity
                .status(httpStatus)
                .body(defaultResponse);
    }
}
