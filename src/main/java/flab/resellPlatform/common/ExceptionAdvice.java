package flab.resellPlatform.common;

import flab.resellPlatform.common.response.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
    public void returnRestRequestError() {
        ThreadLocalStandardResponseBucketHolder.getResponse()
                .setHttpStatus(HttpStatus.BAD_REQUEST);
        ThreadLocalStandardResponseBucketHolder.getResponse().getStandardResponse()
                .setMessage(messageSourceAccessor.getMessage("common.argument.invalid"));
    }
}
