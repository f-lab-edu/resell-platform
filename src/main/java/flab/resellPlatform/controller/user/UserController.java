package flab.resellPlatform.controller.user;

import flab.resellPlatform.controller.response.StandardResponse;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final MessageSourceAccessor messageSourceAccessor;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/create")
    public ResponseEntity createUser(@Valid @RequestBody UserDTO user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Optional<UserDTO> joinedInfo = userService.createUser(user);

        Map<String, Object> returnObjects = Map.of("user", joinedInfo.get());

        StandardResponse defaultResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.join.succeeded"))
                .data(returnObjects)
                .build();

        return ResponseEntity
                .ok()
                .body(defaultResponse);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<StandardResponse> catchDuplicateId(SQLIntegrityConstraintViolationException e) {

        // custom response 생성
        StandardResponse response = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.username.duplicated"))
                .data(Map.of())
                .build();
        return ResponseEntity
                .badRequest()
                .<StandardResponse>body(response);
    }
}