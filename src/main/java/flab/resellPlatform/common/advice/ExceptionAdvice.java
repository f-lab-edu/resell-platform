package flab.resellPlatform.common.advice;

import flab.resellPlatform.common.error.FieldErrors;
import flab.resellPlatform.common.exception.DuplicateUsernameException;
import flab.resellPlatform.common.exception.UserNotFoundException;
import flab.resellPlatform.common.response.StandardResponse;
import flab.resellPlatform.common.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionAdvice {

    private final MessageSource messageSource;
    private final MessageUtil messageUtil;

    @ExceptionHandler(BindException.class)
    public StandardResponse<FieldErrors> handleBindException(BindException bindException, Locale locale) {
        FieldErrors fieldErrors = FieldErrors.create(bindException, messageSource, locale);
        return new StandardResponse<>(messageUtil.getMessage("common.invalid.input"), fieldErrors);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public StandardResponse handleUserNotFoundException() {
        return new StandardResponse(messageUtil.getMessage("user.not.found"));
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public StandardResponse handleDuplicateUsernameException() {
        return new StandardResponse(messageUtil.getMessage("user.duplicate.username"));
    }
}
