package flab.resellPlatform.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StandardResponse<T> {

    private String responseMessage;
    private T data;

    public StandardResponse(String responseMessage) {
        this.responseMessage = responseMessage;
        this.data = null;
    }

}
