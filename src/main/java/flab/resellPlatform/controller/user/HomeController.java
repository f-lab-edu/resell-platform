package flab.resellPlatform.controller.user;

import flab.resellPlatform.controller.response.StandardResponse;
import flab.resellPlatform.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final MessageSourceAccessor messageSourceAccessor;

    @PreAuthorize(Role.USER)
    @PostMapping("/api/user")
    public String testUserAuthority() {
        return "user";
    }

    @PreAuthorize(Role.ADMIN)
    @PostMapping("/api/admin")
    public String testAdminAuthority() {
        return "admin";
    }

    private ResponseEntity<StandardResponse> getStandardResponseEntity(StandardResponse.StandardResponseBuilder standardResponseBuilder, String messageCode, HttpStatus httpStatus) {
        StandardResponse defaultResponse = standardResponseBuilder
                .message(messageSourceAccessor.getMessage(messageCode))
                .build();

        return ResponseEntity
                .status(httpStatus)
                .body(defaultResponse);
    }
}
