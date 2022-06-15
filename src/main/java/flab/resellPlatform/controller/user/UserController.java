package flab.resellPlatform.controller.user;

import flab.resellPlatform.controller.response.DefaultResponse;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.exception.user.DuplicateUsernameException;
import flab.resellPlatform.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final MessageSourceAccessor messageSourceAccessor;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/create")
    public ResponseEntity join(@Valid @RequestBody UserDTO user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Optional<UserDTO> joinedInfo = userService.createUser(user);
        if (joinedInfo.isEmpty()) {
            throw new DuplicateUsernameException();
        }

        Map<String, Object> returnObjects = Map.of("user", joinedInfo.get());

        DefaultResponse defaultResponse = DefaultResponse.builder()
                .messageSummary(messageSourceAccessor.getMessage("createUser.success"))
                .data(returnObjects)
                .build();

        return ResponseEntity
                .ok()
                .body(defaultResponse);
    }
}