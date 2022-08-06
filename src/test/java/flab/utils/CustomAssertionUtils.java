package flab.utils;

import flab.resellPlatform.common.response.StandardResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomAssertionUtils {


    public static void assertStandardResponseBodyContainsData(ObjectMapper objectMapper, ResultActions resultActions, String key) throws IOException {
        MvcResult mvcResult = resultActions.andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        StandardResponse standardResponse = objectMapper.readValue(responseBody, StandardResponse.class);
        assertThat(standardResponse.getData().containsKey(key)).isTrue();
    }

    public static void assertStandardResponseBodyContainsMessage(ObjectMapper objectMapper, ResultActions resultActions, String message) throws IOException {
        MvcResult mvcResult = resultActions.andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        StandardResponse standardResponse = objectMapper.readValue(responseBody, StandardResponse.class);
        assertThat(standardResponse.getMessage()).isEqualTo(message);
    }
}
