package flab.resellPlatform.controller.user;

import flab.resellPlatform.common.response.StandardResponse;
import flab.resellPlatform.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final MessageSourceAccessor messageSourceAccessor;
    private final RedisTemplate<String, Object> redisSessionTemplate;

    @GetMapping("/")
    public StandardResponse getHomePage() {
        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("common.request.succeeded"))
                .data(Map.of("hello", "absSet!"))
                .build();
        redisSessionTemplate.opsForValue().get("abc");
        return standardResponse;
    }

    @GetMapping("/Home")
    public StandardResponse getHome() {
        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("common.request.succeeded"))
                .data(Map.of("hello", "noAbc!"))
                .build();
        return standardResponse;
    }

    @GetMapping("/abc")
    public StandardResponse getAbc() {
        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("common.request.succeeded"))
                .data(Map.of("hello", "abcs!"))
                .build();
        redisSessionTemplate.opsForValue().set("abc", "ddd");
        return standardResponse;
    }

    @PreAuthorize(Role.USER)
    @PostMapping("/api/user")
    public StandardResponse testUserAuthority() {
        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("common.request.succeeded"))
                .data(Map.of("role", "user"))
                .build();

        return standardResponse;
    }

    @PreAuthorize(Role.ADMIN)
    @PostMapping("/api/admin")
    public StandardResponse testAdminAuthority() {
        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("common.request.succeeded"))
                .data(Map.of("role", "admin"))
                .build();
        return standardResponse;
    }

    @PostMapping("/api/noRole")
    public StandardResponse testNoRoleAuthority() {
        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("common.request.succeeded"))
                .data(Map.of("role", "no role"))
                .build();

        return standardResponse;
    }
}
