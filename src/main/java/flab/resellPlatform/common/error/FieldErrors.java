package flab.resellPlatform.common.error;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldErrors {

    private List<FieldErrorDetail> errors;

    public static FieldErrors create(Errors errors, MessageSource messageSource, Locale locale) {
        List<FieldErrorDetail> fieldErrorDetails = errors.getFieldErrors()
                .stream()
                .map(e -> FieldErrorDetail.create(e, messageSource, locale))
                .collect(Collectors.toList());

        return new FieldErrors(fieldErrorDetails);
    }

}
