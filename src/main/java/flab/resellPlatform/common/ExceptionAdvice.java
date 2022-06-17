package flab.resellPlatform.common;

import flab.resellPlatform.controller.response.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
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
    public ResponseEntity<StandardResponse> returnRestRequestError() {

        Map<String, Object> returnObjects = Map.of();

        // custom response 생성
        StandardResponse defaultResponse = getDefaultResponse("common.argument.invalid", returnObjects);
        return ResponseEntity
                .badRequest()
                .<StandardResponse>body(defaultResponse);
    }

    private StandardResponse getDefaultResponse(String code, Map<String, Object> returnObjects) {
        StandardResponse defaultResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage(code))
                .data(returnObjects)
                .build();
        return defaultResponse;
    }
}
