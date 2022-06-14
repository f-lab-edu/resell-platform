package flab.resellPlatform.controller.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class DefaultResponse {
    String messageSummary;
    Object requestDTO;
    Map<String, String> errorMessages;
}
