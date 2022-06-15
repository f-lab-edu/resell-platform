package flab.resellPlatform;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageConfig {

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(
                "classpath:flab/resellPlatform/properties/messages/common",
                "classpath:flab/resellPlatform/properties/messages/createUser",
                "classpath:flab/resellPlatform/properties/messages/home",
                "classpath:flab/resellPlatform/properties/messages/login");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(60);
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    @Bean
    public MessageSourceAccessor messageSourceAccessor () {
        return new MessageSourceAccessor(messageSource());
    }
}
