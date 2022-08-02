package flab.resellPlatform.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StandardResponse {

    private String responseMessage;
    private Object data;

    public StandardResponse(String responseMessage) {
        this.responseMessage = responseMessage;
        this.data = null;
    }

}
