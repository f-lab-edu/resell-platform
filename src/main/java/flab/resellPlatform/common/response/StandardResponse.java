package flab.resellPlatform.common.response;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandardResponse {
    String message;
    Map<String, Object> data;
}
