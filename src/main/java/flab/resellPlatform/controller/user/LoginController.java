package flab.resellPlatform.controller.user;

import flab.resellPlatform.common.SessionConst;
import flab.resellPlatform.domain.user.LoginInfo;
import flab.resellPlatform.service.user.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

    final LoginService loginService;

    @PostMapping
    public ResponseEntity doLogin(@Valid LoginInfo loginInfo, HttpServletRequest request) {
        Optional<LoginInfo> loginAvailableInfo = loginService.doLogin(loginInfo);
        if (loginAvailableInfo.isEmpty()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_INFO, loginInfo);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping
    @RequestMapping("/logout")
    public ResponseEntity doLogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
