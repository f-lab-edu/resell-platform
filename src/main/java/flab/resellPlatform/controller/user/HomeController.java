package flab.resellPlatform.controller.user;

import flab.resellPlatform.common.SessionConst;
import flab.resellPlatform.controller.response.StandardResponse;
import flab.resellPlatform.domain.user.LoginInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final MessageSourceAccessor messageSourceAccessor;

    @PostMapping("/api/user")
    public String testUserAuthority() {
        System.out.println("im in /api/user");
        return "user";
    }

    @PostMapping("/api/admin")
    public String testAdminAuthority() {
        System.out.println("im in /api/admin");
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
