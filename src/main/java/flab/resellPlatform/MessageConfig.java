package flab.resellPlatform;

import flab.resellPlatform.common.util.MessageUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageConfig {

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setBasename("classpath:/messages/message");
        ms.setDefaultEncoding("UTF-8");
        ms.setCacheSeconds(60);
        ms.setUseCodeAsDefaultMessage(true);

        return ms;
    }

    @Bean
    public MessageSourceAccessor messageSourceAccessor() {
        return new MessageSourceAccessor(messageSource());
    }

    @Bean
    public MessageUtil messageUtil() {
        return new MessageUtil(messageSourceAccessor());
    }

}
