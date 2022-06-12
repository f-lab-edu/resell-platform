package flab.resellPlatform.common;

import flab.resellPlatform.controller.response.DefaultResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionAdvice {

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
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<DefaultResponse> returnLoginInfoError(BindingResult me) {
        // 에러 메세지 생성
        Map<String, String> errors = new HashMap<>();
        me.getFieldErrors()
                .forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
        // RequestBody 생성
        Object requestBody = me.getTarget();
        // custom response 생성
        DefaultResponse defaultResponse = DefaultResponse.builder()
                .requestDTO(requestBody)
                .errorMessages(errors)
                .build();
        return ResponseEntity.badRequest().<DefaultResponse>body(defaultResponse);
    }
}
