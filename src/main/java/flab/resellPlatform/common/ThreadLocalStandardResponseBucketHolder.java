package flab.resellPlatform.common;

import flab.resellPlatform.common.response.StandardResponse;
import flab.resellPlatform.common.response.StandardResponseBucket;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalStandardResponseBucketHolder {

    private static final ThreadLocal<StandardResponseBucket> contextHolder = new ThreadLocal<>();

    public static void clearContext() {
        contextHolder.remove();
    }

    public static StandardResponseBucket getResponse() {
        StandardResponseBucket standardResponseBucket = contextHolder.get();
        if (standardResponseBucket == null) {
            standardResponseBucket = StandardResponseBucket.builder()
                    .httpStatus(HttpStatus.OK)
                    .standardResponse(StandardResponse.builder()
                            .data(new HashMap<>())
                            .build())
                    .build();
            contextHolder.set(standardResponseBucket);
        }
        return standardResponseBucket;
    }

    public static void setResponseData(String key, Object value) {
        Map<String, Object> data = getResponse().getStandardResponse().getData();
        data.put(key, value);
    }
}
