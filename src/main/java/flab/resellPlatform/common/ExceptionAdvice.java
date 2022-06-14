package flab.resellPlatform.common;

import flab.resellPlatform.controller.response.DefaultResponse;
import flab.resellPlatform.exception.user.DuplicateUsernameException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionAdvice {

    private final MessageSourceAccessor messageSourceAccessor;

    /*
    * 예시
        {
        *   "errorSummary": 회원가입 실패
            "data": {
                "obj1": "obj data",
                "obj2": "obj data",
                "obj3": "010-4589-1250",
                ...
            },
        }
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<DefaultResponse> returnRestRequestError() {

        Map<String, Object> returnObjects = Map.of();

        // custom response 생성
        DefaultResponse defaultResponse = getDefaultResponse("common.argument.invalid", returnObjects);
        return ResponseEntity
                .badRequest()
                .<DefaultResponse>body(defaultResponse);
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<DefaultResponse> catchDuplicateId(RuntimeException e) {

        Map<String, Object> returnObjects = Map.of();

        // custom response 생성
        DefaultResponse response = getDefaultResponse("user.username.duplication", returnObjects);
        return ResponseEntity
                .badRequest()
                .<DefaultResponse>body(response);
    }

    private DefaultResponse getDefaultResponse(String code, Map<String, Object> returnObjects) {
        DefaultResponse defaultResponse = DefaultResponse.builder()
                .messageSummary(messageSourceAccessor.getMessage(code))
                .data(returnObjects)
                .build();
        return defaultResponse;
    }
}
