package flab.resellPlatform.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import flab.resellPlatform.common.response.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ResponseCreator {

    private final ObjectMapper om;
    private final MessageUtil messageUtil;

    public void createBody(HttpServletResponse response, HttpStatus statusCode, String messageCode, Object data) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode.value());

        String message = messageUtil.getMessage(messageCode);

        StandardResponse standardResponse = new StandardResponse(message, data);

        response.getWriter().write(om.writeValueAsString(standardResponse));
    }

    public void createHeader(HttpServletResponse response, HttpStatus statusCode, String headerName, String messageCode) {
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode.value());
        String message = messageUtil.getMessage(messageCode);
        response.setHeader(headerName, message);
    }

}
