package flab.resellPlatform.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import flab.resellPlatform.common.response.StandardResponse;
import flab.resellPlatform.exception.IORuntimeException;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class ResponseUtils {

    public static void createCustomMessage(HttpServletResponse response, HttpStatus status, StandardResponse standardResponse) {
        // write the data in a response
        try {
            response.setStatus(status.value());
            response.getWriter().write(
                    new ObjectMapper().writeValueAsString(
                            standardResponse));
            response.getWriter().flush();
        } catch (IOException e) {
            throw new IORuntimeException(e.getMessage(), e);
        }
    }

    public static void createCustomMessage(HttpServletResponse response, HttpStatus status) {
        // write the data in a response
        StandardResponse standardResponse = StandardResponse.builder().data(Map.of()).build();
        response.setStatus(status.value());
        try {
            response.getWriter().write(
                    new ObjectMapper().writeValueAsString(
                            standardResponse));
            response.getWriter().flush();
        } catch (IOException e) {
            throw new IORuntimeException(e.getMessage(), e);
        }
    }

}
