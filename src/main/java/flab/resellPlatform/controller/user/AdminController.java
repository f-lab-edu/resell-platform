package flab.resellPlatform.controller.user;

import flab.resellPlatform.common.response.StandardResponse;
import flab.resellPlatform.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize(Role.ADMIN)
public class AdminController {

    @GetMapping
    public StandardResponse testAuthority() {
        StandardResponse standardResponse = StandardResponse.builder()
                .message("Admin Controller test successed")
                .data(Map.of())
                .build();

        return standardResponse;
    }
}
