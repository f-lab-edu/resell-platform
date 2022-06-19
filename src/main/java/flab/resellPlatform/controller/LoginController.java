package flab.resellPlatform.controller;

import flab.resellPlatform.common.SessionConst;
import flab.resellPlatform.common.form.LoginForm;
import flab.resellPlatform.common.response.ResponseMessage;
import flab.resellPlatform.common.response.StandardResponse;
import flab.resellPlatform.domain.User;
import flab.resellPlatform.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @PostMapping("/login")
    public StandardResponse login(@Validated @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletRequest request) throws BindException {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            throw new BindException(bindingResult);
        }

        User loginUser = userService.login(loginForm.getUsername(), loginForm.getPassword());

        if (loginUser == null) {
            return new StandardResponse<>(ResponseMessage.LOGIN_FAILURE);
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_USER, loginUser);

        return new StandardResponse<>(ResponseMessage.LOGIN_SUCCESS);
    }

}
