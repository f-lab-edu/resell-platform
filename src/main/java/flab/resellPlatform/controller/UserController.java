package flab.resellPlatform.controller;

import flab.resellPlatform.common.response.ResponseMessage;
import flab.resellPlatform.common.response.StandardResponse;
import flab.resellPlatform.common.response.account.CreateAccountSuccess;
import flab.resellPlatform.domain.User;
import flab.resellPlatform.domain.UserDTO;
import flab.resellPlatform.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    public StandardResponse<CreateAccountSuccess> createAccount(@Validated @ModelAttribute UserDTO user, BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            throw new BindException(bindingResult);
        }

        User createdUser = userService.createAccount(user);
        CreateAccountSuccess successData = new CreateAccountSuccess(createdUser.getUsername(), createdUser.getName());

        return new StandardResponse<>(ResponseMessage.CREATE_USER_SUCCESS, successData);
    }

    @GetMapping("/users/{userId}")
    public StandardResponse<User> viewAccount(@PathVariable Long userId) {
        return new StandardResponse<>(ResponseMessage.READ_USER_SUCCESS, userService.viewAccount(userId));
    }

    @PutMapping("/users/{userId}")
    public StandardResponse<User> updateAccount(@Validated @ModelAttribute UserDTO user, BindingResult bindingResult, @PathVariable Long userId) throws BindException {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            throw new BindException(bindingResult);
        }

        User updatedUser = userService.updateAccount(userId, user);

        return new StandardResponse<>(ResponseMessage.UPDATE_USER_SUCCESS, updatedUser);
    }

}
