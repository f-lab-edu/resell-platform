package flab.resellPlatform.common;

import flab.resellPlatform.common.response.StandardResponse;
import flab.resellPlatform.common.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
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
    public ResponseEntity<StandardResponse> returnRestRequestError(HttpServletResponse response) {
        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("common.argument.invalid"))
                .data(Map.of())
                .build();

        return new ResponseEntity<>(standardResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardResponse> returnAccessDeniedError(HttpServletResponse response) {
        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("common.access.denied"))
                .data(Map.of())
                .build();

        return new ResponseEntity<>(standardResponse, HttpStatus.FORBIDDEN);
    }
}
