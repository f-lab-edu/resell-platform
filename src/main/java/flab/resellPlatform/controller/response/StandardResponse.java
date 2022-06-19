package flab.resellPlatform.controller.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class StandardResponse {
    String message;
    Map<String, Object> data;
}
