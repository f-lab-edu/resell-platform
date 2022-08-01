package flab.resellPlatform.controller.user;

import flab.resellPlatform.common.utils.ResponseUtils;
import flab.resellPlatform.common.utils.UserUtils;
import flab.resellPlatform.common.response.StandardResponse;
import flab.resellPlatform.domain.user.*;
import flab.resellPlatform.exception.user.PhoneNumberNotFoundException;
import flab.resellPlatform.exception.user.UserInfoNotFoundException;
import flab.resellPlatform.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
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
    public StandardResponse createUser(@Valid @RequestBody UserDTO user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPhoneNumber(UserUtils.normalizePhoneNumber(user.getPhoneNumber()));
        Optional<UserDTO> joinedInfo = userService.createUser(user);

        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.join.succeeded"))
                .data(Map.of())
                .build();

        return standardResponse;
    }

    @GetMapping("/usernameInquiry")
    public StandardResponse findUsername(String phoneNumber) {
        String normalizedPhoneNumber = UserUtils.normalizePhoneNumber(phoneNumber);
        Optional<String> result = userService.findUsername(normalizedPhoneNumber);
        if (result.isEmpty()) {
            throw new PhoneNumberNotFoundException();
        }

        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.username.found"))
                .data(Map.of("username", result.get()))
                .build();

        return standardResponse;
    }

    @PostMapping("/password/inquiry")
    public StandardResponse findPassword(@Valid @RequestBody StrictLoginInfo strictLoginInfo) {
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

        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.temporary.password.returned"))
                .data(Map.of("password", randomGeneratedPassword))
                .build();

        return standardResponse;
    }

    @PreAuthorize(Role.USER)
    @PostMapping("/password/update")
    public StandardResponse updatePassword(LoginInfo newLoginInfo) {
        String encodedPassword = passwordEncoder.encode(newLoginInfo.getPassword());
        newLoginInfo.setPassword(encodedPassword);

        int result = userService.updatePassword(newLoginInfo);
        if (result == 0) {
            throw new UserInfoNotFoundException();
        }

        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.password.update.succeeded"))
                .data(Map.of())
                .build();

        return standardResponse;
    }

    @ExceptionHandler(PhoneNumberNotFoundException.class)
    public ResponseEntity<StandardResponse> catchDuplicateId(PhoneNumberNotFoundException e, HttpServletResponse response) {
        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.phoneNumber.notFound"))
                .data(Map.of())
                .build();

        return new ResponseEntity<>(standardResponse, HttpStatus.BAD_REQUEST);
    }

    /*
    *
    * message: "회원가입 실패"
    * data : {
    *   object1:
    *   object2:
    * }
     */

    @ExceptionHandler(UserInfoNotFoundException.class)
    public ResponseEntity<StandardResponse> catchDuplicateId(UserInfoNotFoundException e, HttpServletResponse response) {
        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.userInfo.notFound"))
                .data(Map.of())
                .build();

        return new ResponseEntity<>(standardResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<StandardResponse> catchDuplicateId(DuplicateKeyException e, HttpServletResponse response) {
        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.username.duplicated"))
                .data(Map.of())
                .build();

        return new ResponseEntity<>(standardResponse, HttpStatus.BAD_REQUEST);
    }
}