package flab.resellPlatform.controller.user;

import flab.resellPlatform.controller.response.StandardResponse;
import flab.resellPlatform.domain.user.PasswordInquiryForm;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.exception.user.PhoneNumberNotFoundException;
import flab.resellPlatform.exception.user.UserInfoNotFoundException;
import flab.resellPlatform.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.sql.SQLIntegrityConstraintViolationException;
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
    public ResponseEntity createUser(@Valid @RequestBody UserDTO user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Optional<UserDTO> joinedInfo = userService.createUser(user);

        StandardResponse defaultResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.join.succeeded"))
                .data(Map.of())
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