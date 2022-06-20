package flab.resellPlatform.controller;

import flab.resellPlatform.common.exception.UserNotFoundException;
import flab.resellPlatform.common.response.StandardResponse;
import flab.resellPlatform.common.response.account.CreateAccountSuccess;
import flab.resellPlatform.common.util.MessageUtil;
import flab.resellPlatform.domain.User;
import flab.resellPlatform.domain.UserDTO;
import flab.resellPlatform.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MessageUtil messageUtil;

    @PostMapping("/users")
    public StandardResponse<CreateAccountSuccess> createAccount(@Validated @ModelAttribute UserDTO user, BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            throw new BindException(bindingResult);
        }

        User createdUser = userService.createAccount(user);
        CreateAccountSuccess successData = new CreateAccountSuccess(createdUser.getUsername(), createdUser.getName());

        return new StandardResponse<>(messageUtil.getMessage("user.signup.success"), successData);
    }

    @GetMapping("/users/{userId}")
    public StandardResponse<User> viewAccount(@PathVariable Long userId) {
        Optional<User> user = userService.viewAccount(userId);
        return new StandardResponse<>(messageUtil.getMessage("user.view.success"), user.orElseThrow(() -> new UserNotFoundException()));
    }

    @PutMapping("/users/{userId}")
    public StandardResponse<User> updateAccount(@Validated @ModelAttribute UserDTO user, BindingResult bindingResult, @PathVariable Long userId) throws BindException {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            throw new BindException(bindingResult);
        }

        User updatedUser = userService.updateAccount(userId, user);

        return new StandardResponse<>(messageUtil.getMessage("user.update.success"), updatedUser);
    }

}
