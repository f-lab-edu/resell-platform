package flab.resellPlatform.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class IORuntimeException extends RuntimeException {
    public IORuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
