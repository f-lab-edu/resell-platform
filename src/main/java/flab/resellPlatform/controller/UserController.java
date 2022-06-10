package flab.resellPlatform.controller;

import flab.resellPlatform.common.SessionConst;
import flab.resellPlatform.common.error.FieldErrors;
import flab.resellPlatform.common.response.ResponseMessage;
import flab.resellPlatform.common.response.StandardResponse;
import flab.resellPlatform.common.response.StatusCode;
import flab.resellPlatform.common.response.account.CreateAccountSuccess;
import flab.resellPlatform.domain.User;
import flab.resellPlatform.service.UserService;
import flab.resellPlatform.common.form.LoginForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MessageSource messageSource;

    @ExceptionHandler(BindException.class)
    public StandardResponse<FieldErrors> handleBindException(BindException bindException, Locale locale) {
        FieldErrors fieldErrors = FieldErrors.create(bindException, messageSource, locale);
        return new StandardResponse<>(StatusCode.BAD_REQUEST, ResponseMessage.INVALID_INPUT, fieldErrors);
    }

    @PostMapping("/users")
    public StandardResponse<CreateAccountSuccess> createAccount(@Validated @ModelAttribute User user, BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            throw new BindException(bindingResult);
        }

        User createdUser = userService.createAccount(user);
        CreateAccountSuccess successData = new CreateAccountSuccess(createdUser.getUsername(), createdUser.getName());

        return new StandardResponse<>(StatusCode.CREATED, ResponseMessage.CREATE_USER_SUCCESS, successData);
    }

    @GetMapping("/users/{userId}")
    public StandardResponse<User> viewAccount(@PathVariable Long userId) {
        return new StandardResponse<>(StatusCode.OK, ResponseMessage.READ_USER_SUCCESS, userService.viewAccount(userId));
    }

    @PostMapping("/users/{userId}")
    public StandardResponse<User> updateAccount(@Validated @ModelAttribute User user, BindingResult bindingResult, @PathVariable Long userId) throws BindException {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            throw new BindException(bindingResult);
        }

        User updatedUser = userService.updateAccount(userId, user);

        return new StandardResponse<>(StatusCode.OK, ResponseMessage.UPDATE_USER_SUCCESS, updatedUser);
    }

    @PostMapping("/login")
    public StandardResponse login(@Validated @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletRequest request) throws BindException {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            throw new BindException(bindingResult);
        }

        User loginUser = userService.login(loginForm.getUsername(), loginForm.getPassword());

        if (loginUser == null) {
            return new StandardResponse<>(StatusCode.BAD_REQUEST, ResponseMessage.LOGIN_FAILURE);
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_USER, loginUser);

        return new StandardResponse<>(StatusCode.OK, ResponseMessage.LOGIN_SUCCESS);
    }

}
