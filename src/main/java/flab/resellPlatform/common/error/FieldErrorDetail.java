package flab.resellPlatform.common.error;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.validation.FieldError;

import java.util.Locale;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldErrorDetail {

    private String fieldName;
    private String code;
    private Object rejectedValue;
    private String message;

    public static FieldErrorDetail create(FieldError fieldError, MessageSource messageSource, Locale locale) {
        return new FieldErrorDetail(
                fieldError.getField(),
                fieldError.getCode(),
                fieldError.getRejectedValue(),
                messageSource.getMessage(fieldError, locale));
    }

}
