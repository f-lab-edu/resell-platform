package flab.resellPlatform.common.advice;

import flab.resellPlatform.common.error.FieldErrors;
import flab.resellPlatform.common.response.ResponseMessage;
import flab.resellPlatform.common.response.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class BindingExceptionAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(BindException.class)
    public StandardResponse<FieldErrors> handleBindException(BindException bindException, Locale locale) {
        FieldErrors fieldErrors = FieldErrors.create(bindException, messageSource, locale);
        return new StandardResponse<>(ResponseMessage.INVALID_INPUT, fieldErrors);
    }

}
