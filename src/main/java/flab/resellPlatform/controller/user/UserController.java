package flab.resellPlatform.controller.user;

import flab.resellPlatform.controller.response.UserDTOErrorMsg;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<String> create(@Valid @RequestBody UserDTO user) {
        userService.join(user);
        return ResponseEntity.ok().body("UserDTO 객체 검증 성공");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> catchInvalidCreateInput(MethodArgumentNotValidException me) {
        Map<String, String> errors = new HashMap<>();
        me.getFieldErrors()
                .forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}