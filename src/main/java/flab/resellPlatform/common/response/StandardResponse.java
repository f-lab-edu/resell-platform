package flab.resellPlatform.common.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class StandardResponse {
    String message;
    Map<String, Object> data;
}
