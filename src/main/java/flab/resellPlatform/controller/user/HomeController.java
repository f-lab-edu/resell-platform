package flab.resellPlatform.controller.user;

import flab.resellPlatform.common.SessionConst;
import flab.resellPlatform.controller.response.StandardResponse;
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

        StandardResponse.StandardResponseBuilder standardResponseBuilder = StandardResponse.builder()
                .data(Map.of());

        if (session == null) {
            return getStandardResponseEntity(standardResponseBuilder, "common.login.need", HttpStatus.UNAUTHORIZED);
        }

        LoginInfo loginInfo = (LoginInfo) session.getAttribute(SessionConst.LOGIN_INFO);
        if (loginInfo == null) {
            return getStandardResponseEntity(standardResponseBuilder, "common.login.need", HttpStatus.UNAUTHORIZED);
        }

        return getStandardResponseEntity(standardResponseBuilder, "home.welcome", HttpStatus.OK);
    }

    private ResponseEntity<StandardResponse> getStandardResponseEntity(StandardResponse.StandardResponseBuilder standardResponseBuilder, String messageCode, HttpStatus httpStatus) {
        StandardResponse defaultResponse = standardResponseBuilder
                .message(messageSourceAccessor.getMessage(messageCode))
                .build();

        return ResponseEntity
                .status(httpStatus)
                .body(defaultResponse);
    }
}
