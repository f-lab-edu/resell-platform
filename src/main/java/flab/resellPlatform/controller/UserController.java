package flab.resellPlatform.controller;

import flab.resellPlatform.domain.User;
import flab.resellPlatform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    public String createAccount(@ModelAttribute User user) {
        Long userId = userService.createAccount(user);
        return "New User: [" + userId.toString() + "] " + user.getName();
    }

    @GetMapping("/users/{userId}")
    public User viewAccount(@PathVariable Long userId) {
        return userService.viewAccount(userId);
    }

    @PutMapping("/users/{userId}")
    public User updateAccount(@ModelAttribute User user, @PathVariable Long userId) {
        return userService.updateAccount(userId, user);
    }

}
