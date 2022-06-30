package flab.resellPlatform.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import flab.resellPlatform.common.response.StandardResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class TestUtil {
    public static void expectDefaultResponse(ObjectMapper mapper, StandardResponse standardResponse, ResultMatcher status, ResultActions resultActions) throws Exception {
        String defaultResponseJson = mapper.writeValueAsString(standardResponse);
        resultActions
                .andExpect(status)
                .andExpect(content().string(defaultResponseJson));
    }
}
