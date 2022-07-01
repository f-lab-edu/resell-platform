package flab.resellPlatform.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
public class MessageUtil {

    private final MessageSourceAccessor msa;

    public String getMessage(String messageKey) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return msa.getMessage(messageKey, request.getLocale());
    }
}
