package flab.resellPlatform.controller.user;

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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @PostMapping("/create")
    public StandardResponse createUser(@Valid @RequestBody UserDTO user) {
        userService.createUser(user);

        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.join.succeeded"))
                .data(Map.of())
                .build();

        return standardResponse;
    }

    @GetMapping("/usernameInquiry")
    public StandardResponse findUsername(String phoneNumber) {
        String result = userService.findUsername(phoneNumber);

        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.username.found"))
                .data(Map.of("username", result))
                .build();

        return standardResponse;
    }

    @PostMapping("/password/inquiry")
    public StandardResponse findPassword(@Valid @RequestBody StrictLoginInfo strictLoginInfo) {
        // 비밀번호 업데이트
        String result = userService.updatePassword(strictLoginInfo);

        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.temporary.password.returned"))
                .data(Map.of("password", result))
                .build();

        return standardResponse;
    }

    @PreAuthorize(Role.USER)
    @PostMapping("/password/update")
    public StandardResponse updatePassword(@Valid @RequestBody LoginInfo newLoginInfo) {
        userService.updatePassword(newLoginInfo);

        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.password.update.succeeded"))
                .data(Map.of())
                .build();

        return standardResponse;
    }

    /*
    *
    * message: "회원가입 실패"
    * data : {
    *   object1:
    *   object2:
    * }
     */

    @ExceptionHandler(PhoneNumberNotFoundException.class)
    public ResponseEntity<StandardResponse> catchDuplicateId(PhoneNumberNotFoundException e, HttpServletResponse response) {
        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.phoneNumber.notFound"))
                .data(Map.of())
                .build();

        return new ResponseEntity<>(standardResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<StandardResponse> catchDuplicateId(UsernameNotFoundException e, HttpServletResponse response) {
        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("user.username.notFound"))
                .data(Map.of())
                .build();

        return new ResponseEntity<>(standardResponse, HttpStatus.BAD_REQUEST);
    }


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