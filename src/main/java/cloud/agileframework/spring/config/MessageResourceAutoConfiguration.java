package cloud.agileframework.spring.config;

import cloud.agileframework.common.util.properties.PropertiesUtil;
import cloud.agileframework.spring.properties.ApplicationProperties;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.time.Duration;
import java.util.stream.Collectors;

/**
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
@EnableConfigurationProperties(ApplicationProperties.class)
@AutoConfigureBefore(MessageSourceAutoConfiguration.class)
@Configuration
public class MessageResourceAutoConfiguration {
    @Bean
    @ConfigurationProperties(
            prefix = "spring.messages"
    )
    @ConditionalOnMissingBean(MessageSourceProperties.class)
    public MessageSourceProperties messageSourceProperties() {
        return new MessageSourceProperties();
    }

    @Bean
    public MessageSource messageSource(MessageSourceProperties properties) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        String[] baseNames = PropertiesUtil.getFileNames()
                .stream()
                .filter(name -> name.contains(properties.getBasename()))
                .map(name -> name.substring(1, name.indexOf(properties.getBasename())) + properties.getBasename())
                .collect(Collectors.toSet())
                .toArray(new String[]{});

        messageSource.setBasenames(baseNames);
        if (properties.getEncoding() != null) {
            messageSource.setDefaultEncoding(properties.getEncoding().name());
        }

        messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
        Duration cacheDuration = properties.getCacheDuration();
        if (cacheDuration != null) {
            messageSource.setCacheMillis(cacheDuration.toMillis());
        }
        messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
        messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());
        return messageSource;
    }
}
