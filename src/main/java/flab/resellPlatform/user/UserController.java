package flab.resellPlatform.user;

import flab.resellPlatform.user.domain.UserDTO;
import flab.resellPlatform.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public String create(@ModelAttribute UserDTO user) {
//        System.out.println(user.getName());
        userService.join(user);
        return "haha";
    }
}