package flab.resellPlatform.controller.user;

import flab.resellPlatform.common.ThreadLocalStandardResponseBucketHolder;
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
    public void createUser(@Valid @RequestBody UserDTO user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPhoneNumber(UserUtils.normalizePhoneNumber(user.getPhoneNumber()));
        Optional<UserDTO> joinedInfo = userService.createUser(user);

        ThreadLocalStandardResponseBucketHolder.getResponse().getStandardResponse()
                .setMessage(messageSourceAccessor.getMessage("user.join.succeeded"));
    }

    @GetMapping("/usernameInquiry")
    public void findUsername(String phoneNumber) {
        String normalizedPhoneNumber = UserUtils.normalizePhoneNumber(phoneNumber);
        Optional<String> result = userService.findUsername(normalizedPhoneNumber);
        if (result.isEmpty()) {
            throw new PhoneNumberNotFoundException();
        }


        ThreadLocalStandardResponseBucketHolder.setResponseData("username", result.get());
        ThreadLocalStandardResponseBucketHolder.getResponse().getStandardResponse()
                .setMessage(messageSourceAccessor.getMessage("user.username.found"));
    }

    @PostMapping("/password/inquiry")
    public void findPassword(@Valid @RequestBody StrictLoginInfo strictLoginInfo) {
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

        ThreadLocalStandardResponseBucketHolder.setResponseData("password", randomGeneratedPassword);
        ThreadLocalStandardResponseBucketHolder.getResponse().getStandardResponse()
                .setMessage(messageSourceAccessor.getMessage("user.temporary.password.returned"));
    }

    @PreAuthorize(Role.USER)
    @PostMapping("/password/update")
    public void updatePassword(LoginInfo newLoginInfo) {

        String encodedPassword = passwordEncoder.encode(newLoginInfo.getPassword());
        newLoginInfo.setPassword(encodedPassword);

        int result = userService.updatePassword(newLoginInfo);
        if (result == 0) {
            throw new UserInfoNotFoundException();
        }

        ThreadLocalStandardResponseBucketHolder.getResponse().getStandardResponse()
                .setMessage(messageSourceAccessor.getMessage("user.password.updated.succeeded"));
    }

    @ExceptionHandler(PhoneNumberNotFoundException.class)
    public void catchDuplicateId(PhoneNumberNotFoundException e) {

        ThreadLocalStandardResponseBucketHolder.getResponse()
                        .setHttpStatus(HttpStatus.BAD_REQUEST);
        ThreadLocalStandardResponseBucketHolder.getResponse().getStandardResponse()
                .setMessage(messageSourceAccessor.getMessage("user.phoneNumber.notFound"));
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
    public void catchDuplicateId(UserInfoNotFoundException e) {

        ThreadLocalStandardResponseBucketHolder.getResponse()
                .setHttpStatus(HttpStatus.BAD_REQUEST);
        ThreadLocalStandardResponseBucketHolder.getResponse().getStandardResponse()
                .setMessage(messageSourceAccessor.getMessage("user.userInfo.notFound"));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public void catchDuplicateId(DuplicateKeyException e) {

        ThreadLocalStandardResponseBucketHolder.getResponse()
                .setHttpStatus(HttpStatus.BAD_REQUEST);
        ThreadLocalStandardResponseBucketHolder.getResponse().getStandardResponse()
                .setMessage(messageSourceAccessor.getMessage("user.username.duplicated"));
    }
}