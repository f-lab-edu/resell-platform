package flab.resellPlatform.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PropertyUtil {
    public static String getProperty(String propertyName) {
        return getProperty(propertyName, null);
    }
    public static String getProperty(String propertyName, String defaultValue) {
        String value = defaultValue;
        ApplicationContext applicationContext = ApplicationContextServe.getApplicationContext();
        if(applicationContext.getEnvironment().getProperty(propertyName) == null) {
            log.warn(propertyName + " properties was not loaded.");
        } else {
            value = applicationContext.getEnvironment().getProperty(propertyName).toString();
        }
        return value;
    }
}