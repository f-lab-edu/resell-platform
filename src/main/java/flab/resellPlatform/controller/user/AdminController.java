package flab.resellPlatform.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@Secured("ROLE_ADMIN")
@RequiredArgsConstructor
public class AdminController {

    @GetMapping
    public ResponseEntity testAuthority() {
        return ResponseEntity.ok().build();
    }
}
