package flab.resellPlatform.controller.user;

import flab.resellPlatform.common.utils.UserUtils;
import flab.resellPlatform.controller.response.StandardResponse;
import flab.resellPlatform.domain.user.LoginInfo;
import flab.resellPlatform.domain.user.PasswordInquiryForm;
import flab.resellPlatform.domain.user.StrictLoginInfo;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.exception.user.PhoneNumberNotFoundException;
import flab.resellPlatform.exception.user.UserInfoNotFoundException;
import flab.resellPlatform.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.web.bind.annotation.*;

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
    private final RandomValueStringGenerator randomValueStringGenerator;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/create")
    public ResponseEntity createUser(@Valid @RequestBody UserDTO user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPhoneNumber(UserUtils.normalizePhoneNumber(user.getPhoneNumber()));
        Optional<UserDTO> joinedInfo = userService.createUser(user);

        StandardResponse defaultResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.join.succeeded"))
                .data(Map.of())
                .build();

        return ResponseEntity
                .ok()
                .body(defaultResponse);
    }

    @GetMapping("/usernameInquiry")
    public ResponseEntity findUsername(String phoneNumber) {
        String normalizedPhoneNumber = UserUtils.normalizePhoneNumber(phoneNumber);
        Optional<String> result = userService.findUsername(normalizedPhoneNumber);
        if (result.isEmpty()) {
            throw new PhoneNumberNotFoundException();
        }

        StandardResponse response = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.username.found"))
                .data(Map.of("username", result.get()))
                .build();

        return ResponseEntity
                .ok()
                .body(response);
    }

    @PostMapping("/password/inquiry")
    public ResponseEntity findPassword(@Valid @RequestBody StrictLoginInfo strictLoginInfo) {
        strictLoginInfo.setPhoneNumber(UserUtils.normalizePhoneNumber(strictLoginInfo.getPhoneNumber()));

        // 임시 비밀번호 생성 
        String randomGeneratedPassword = randomValueStringGenerator.generate();
        String encodedPassword = passwordEncoder.encode(randomGeneratedPassword);
        strictLoginInfo.setPassword(encodedPassword);
        
        // 비밀번호 업데이트
        int result = userService.updatePassword(strictLoginInfo);

        if (result == 0) {
            throw new UserInfoNotFoundException();
        }

        StandardResponse response = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.temporary.password.returned"))
                .data(Map.of("password", randomGeneratedPassword))
                .build();

        return ResponseEntity
                .ok()
                .body(response);
    }

    @PostMapping("/password/update")
    public ResponseEntity updatePassword(LoginInfo newLoginInfo) {

        String encodedPassword = passwordEncoder.encode(newLoginInfo.getPassword());
        newLoginInfo.setPassword(encodedPassword);

        int result = userService.updatePassword(newLoginInfo);
        if (result == 0) {
            throw new UserInfoNotFoundException();
        }

        StandardResponse response = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.password.updated.succeeded"))
                .data(Map.of())
                .build();

        return ResponseEntity
                .ok()
                .body(response);
    }

    @ExceptionHandler(PhoneNumberNotFoundException.class)
    public ResponseEntity<StandardResponse> catchDuplicateId(PhoneNumberNotFoundException e) {

        // custom response 생성
        StandardResponse response = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.phoneNumber.notFound"))
                .data(Map.of())
                .build();
        return ResponseEntity
                .badRequest()
                .<StandardResponse>body(response);
    }

    @ExceptionHandler(UserInfoNotFoundException.class)
    public ResponseEntity<StandardResponse> catchDuplicateId(UserInfoNotFoundException e) {

        // custom response 생성
        StandardResponse response = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.userInfo.notFound"))
                .data(Map.of())
                .build();
        return ResponseEntity
                .badRequest()
                .<StandardResponse>body(response);
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