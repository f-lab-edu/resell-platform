package flab.resellPlatform.controller.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class DefaultResponse {
    String messageSummary;
    Map<String, Object> data;
}
