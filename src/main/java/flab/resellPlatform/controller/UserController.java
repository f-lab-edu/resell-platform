package flab.resellPlatform.controller;

import flab.resellPlatform.domain.User;
import flab.resellPlatform.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    public String createAccount(@Validated @ModelAttribute User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "Invalid Input!!!";
        }

        Long userId = userService.createAccount(user);
        return "New User: [" + userId.toString() + "] " + user.getName();
    }

    @GetMapping("/users/{userId}")
    public User viewAccount(@PathVariable Long userId) {
        return userService.viewAccount(userId);
    }

    @PostMapping("/users/{userId}")
    public User updateAccount(@Validated @ModelAttribute User user, BindingResult bindingResult, @PathVariable Long userId) {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return null;
        }

        return userService.updateAccount(userId, user);
    }

}
