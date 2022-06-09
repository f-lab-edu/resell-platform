package flab.resellPlatform.controller.user;

import flab.resellPlatform.controller.response.DefaultResponse;
import flab.resellPlatform.controller.response.UserDTOErrorMsg;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

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
    public String create(@Valid @RequestBody UserDTO user, WebRequest webRequest) {
        webRequest.setAttribute("user", user, RequestAttributes.SCOPE_REQUEST);
        userService.join(user);
        return "create success";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<DefaultResponse<UserDTO>> catchDuplicateId(IllegalArgumentException e, WebRequest webRequest) {
        // 에러 메세지 생성
        HashMap<String, String> errors = new HashMap<>();
        errors.put("username", "이미 존재하는 아이디입니다.");
        // RequestBody 생성
        UserDTO requestBody = (UserDTO) webRequest.getAttribute("user", RequestAttributes.SCOPE_REQUEST);

        /*
        * 예시
        {
            "requestDTO": {
                "username": "ms",
                "password": "bb",
                "phoneNumber": "010-4589-1252",
                ...
            },
            "errorMessages": {
                "username": "이미 존재하는 아이디입니다."
            }
        }
        */
        // custom response 생성
        DefaultResponse<UserDTO> response = DefaultResponse.<UserDTO>builder()
                .requestDTO(requestBody)
                .errorMessages(errors)
                .build();
        return ResponseEntity.badRequest().<DefaultResponse<UserDTO>>body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DefaultResponse<UserDTO>> catchInvalidCreateInput(MethodArgumentNotValidException me) {
        // 에러 메세지 생성
        Map<String, String> errors = new HashMap<>();
        me.getFieldErrors()
                .forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
        // RequestBody 생성
        UserDTO requestBody = (UserDTO)me.getTarget();

        // custom response 생성
        DefaultResponse<UserDTO> defaultResponse = DefaultResponse.<UserDTO>builder()
                .requestDTO(requestBody)
                .errorMessages(errors)
                .build();
        /*
        * 예시
        {
            "requestDTO": {
                "username": "a",
                "password": "",
                "phoneNumber": "010-4589-1250",
                ...
            },
            "errorMessages": {
                "password": "must not be blank"
            }
        }
        */
        return ResponseEntity.badRequest().<DefaultResponse<UserDTO>>body(defaultResponse);
    }
}