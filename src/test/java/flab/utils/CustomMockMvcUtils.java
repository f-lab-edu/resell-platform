package flab.utils;

import flab.resellPlatform.common.response.StandardResponse;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CustomMockMvcUtils {

    public static String getAccessTokenDataByMockMvcLogin(MockMvc mockMvc, String content, ObjectMapper objectMapper, Environment environment) throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        StandardResponse standardResponse = objectMapper.readValue(responseBody, StandardResponse.class);
        String accessTokenData = (String)standardResponse.getData().get(environment.getProperty("jwt.token.type.access"));
        return accessTokenData;
    }
}
