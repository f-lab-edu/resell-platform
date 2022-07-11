package flab.resellPlatform.controller.user;

import flab.resellPlatform.common.ThreadLocalStandardResponseBucketHolder;
import flab.resellPlatform.common.response.StandardResponse;
import flab.resellPlatform.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final MessageSourceAccessor messageSourceAccessor;

    @GetMapping("/")
    public void getHomePage() {
        ThreadLocalStandardResponseBucketHolder.setResponseData("hello", "world");
    }

    @PreAuthorize(Role.USER)
    @PostMapping("/api/user")
    public void testUserAuthority() {
        ThreadLocalStandardResponseBucketHolder.setResponseData("role", "user");
    }

    @PreAuthorize(Role.ADMIN)
    @PostMapping("/api/admin")
    public void testAdminAuthority() {

        ThreadLocalStandardResponseBucketHolder.setResponseData("role", "admin");
    }

    @PostMapping("/api/noRole")
    public void testNoRoleAuthority() {
        ThreadLocalStandardResponseBucketHolder.setResponseData("role", "no role");
    }
}
