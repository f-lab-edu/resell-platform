package flab.resellPlatform.controller.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Getter
@Builder
public class StandardResponse {
    String message;
    Map<String, Object> data;
}
