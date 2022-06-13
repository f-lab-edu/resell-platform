package flab.resellPlatform.controller.user;

import flab.resellPlatform.controller.response.DefaultResponse;
import flab.resellPlatform.domain.user.UserDTO;
import flab.resellPlatform.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity create(@Valid @RequestBody UserDTO user, WebRequest webRequest) {
        webRequest.setAttribute("user", user, RequestAttributes.SCOPE_REQUEST);
        Optional<UserDTO> joinedInfo = userService.join(user);
        if (joinedInfo.isEmpty()) {
            throw new IllegalArgumentException("http의 대표 메세지로 나옴");
        }

        return new ResponseEntity(HttpStatus.OK);
    }

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
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<DefaultResponse> catchDuplicateId(IllegalArgumentException e, WebRequest webRequest) {
        // 에러 메세지 생성
        HashMap<String, String> errors = new HashMap<>();
        errors.put("username", UserDTO.errorMessage.USERNAME_DUPLICATION);

        // RequestBody 생성
        Object requestBody = webRequest.getAttribute("user", RequestAttributes.SCOPE_REQUEST);

        // custom response 생성
        DefaultResponse response = DefaultResponse.builder()
                .requestDTO(requestBody)
                .errorMessages(errors)
                .build();
        return ResponseEntity.badRequest().<DefaultResponse>body(response);
    }
}