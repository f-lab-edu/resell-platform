package flab.resellPlatform.controller.response;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public class DefaultResponse<T> {
    HttpStatus status;
    String message;
    T data;
}
