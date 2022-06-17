package flab.resellPlatform.controller.user;

import flab.resellPlatform.common.SessionConst;
import flab.resellPlatform.controller.response.StandardResponse;
import flab.resellPlatform.domain.user.LoginInfo;
import flab.resellPlatform.service.user.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final MessageSourceAccessor messageSourceAccessor;
    private final LoginService loginService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity doLogin(@Valid LoginInfo loginInfo, HttpServletRequest request) {

        loginInfo.setPassword(passwordEncoder.encode(loginInfo.getPassword()));

        Optional<LoginInfo> loginAvailableInfo = loginService.doLogin(loginInfo);
        if (loginAvailableInfo.isEmpty()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_INFO, loginInfo);

        Map<String, Object> returnObjects = Map.of("loginInfo", loginAvailableInfo.get());
        StandardResponse defaultResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("login.success"))
                .data(returnObjects)
                .build();

        return ResponseEntity
                .ok()
                .body(defaultResponse);
    }

    @PostMapping
    @RequestMapping("/logout")
    public ResponseEntity doLogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Map<String, Object> returnObjects = Map.of();
        StandardResponse defaultResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("login.logout.success"))
                .data(returnObjects)
                .build();

        return ResponseEntity
                .ok()
                .body(defaultResponse);
    }
}
