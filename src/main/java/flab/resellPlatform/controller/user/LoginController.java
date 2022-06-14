package flab.resellPlatform.controller.user;

import flab.resellPlatform.common.SessionConst;
import flab.resellPlatform.controller.response.DefaultResponse;
import flab.resellPlatform.domain.user.LoginInfo;
import flab.resellPlatform.service.user.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

    final MessageSourceAccessor messageSourceAccessor;
    final LoginService loginService;

    @PostMapping
    public ResponseEntity doLogin(@Valid LoginInfo loginInfo, HttpServletRequest request) {
        Optional<LoginInfo> loginAvailableInfo = loginService.doLogin(loginInfo);
        if (loginAvailableInfo.isEmpty()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_INFO, loginInfo);

        Map<String, Object> returnObjects = Map.of("loginInfo", loginAvailableInfo.get());
        DefaultResponse defaultResponse = DefaultResponse.builder()
                .messageSummary(messageSourceAccessor.getMessage("user.login.success"))
                .data(returnObjects)
                .build();

        return ResponseEntity.ok(defaultResponse);
    }

    @PostMapping
    @RequestMapping("/logout")
    public ResponseEntity doLogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Map<String, Object> returnObjects = Map.of();
        DefaultResponse defaultResponse = DefaultResponse.builder()
                .messageSummary(messageSourceAccessor.getMessage("user.logout.success"))
                .data(returnObjects)
                .build();

        return ResponseEntity
                .ok()
                .<DefaultResponse>body(defaultResponse);
    }
}
