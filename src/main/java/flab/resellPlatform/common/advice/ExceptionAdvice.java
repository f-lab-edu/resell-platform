package flab.resellPlatform.common.advice;

import flab.resellPlatform.common.error.FieldErrors;
import flab.resellPlatform.common.exception.DuplicateUsernameException;
import flab.resellPlatform.common.exception.UserNotFoundException;
import flab.resellPlatform.common.response.StandardResponse;
import flab.resellPlatform.common.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionAdvice {

    private final MessageSource messageSource;
    private final MessageUtil messageUtil;

    @ExceptionHandler(BindException.class)
    public StandardResponse<FieldErrors> handleBindException(BindException bindException, Locale locale, HttpServletResponse response) {
        FieldErrors fieldErrors = FieldErrors.create(bindException, messageSource, locale);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return new StandardResponse<>(messageUtil.getMessage("common.invalid.input"), fieldErrors);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public StandardResponse handleUserNotFoundException(HttpServletResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return new StandardResponse(messageUtil.getMessage("user.not.found"));
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public StandardResponse handleDuplicateUsernameException(HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return new StandardResponse(messageUtil.getMessage("user.duplicate.username"));
    }
}
