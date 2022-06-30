package flab.resellPlatform.common.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
public class StandardResponseBucket {
    HttpStatus httpStatus;
    StandardResponse standardResponse;
}
