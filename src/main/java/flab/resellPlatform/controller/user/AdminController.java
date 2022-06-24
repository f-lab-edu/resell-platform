package flab.resellPlatform.controller.user;

import flab.resellPlatform.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize(Role.ADMIN)
public class AdminController {

    @GetMapping
    public ResponseEntity testAuthority() {
        return ResponseEntity.ok().build();
    }
}
