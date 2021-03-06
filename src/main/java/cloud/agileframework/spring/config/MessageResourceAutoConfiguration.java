package cloud.agileframework.spring.config;

import cloud.agileframework.spring.properties.ApplicationProperties;
import cloud.agileframework.spring.util.ResourceUtil;
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
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
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
    public MessageSource messageSource(MessageSourceProperties properties) throws UnsupportedEncodingException {

        String[] basenameSource = StringUtils
                .commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(properties.getBasename()) + ",cloud/agileframework/*/message");

        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        String rootPath = URLDecoder.decode(this.getClass().getResource("/").getPath(), Charset.defaultCharset().name());
        String[] baseNames = Arrays.stream(basenameSource)
                .map(basename -> ResourceUtil.getResources(basename, "properties"))
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .map(resource -> {
                    try {
                        final URL url = resource.getURL();
                        String path = URLDecoder.decode(url.getPath(), Charset.defaultCharset().name());
                        final String jar = ".jar!/";
                        if (ResourceUtils.isJarURL(url) && path.contains(jar)) {
                            return path.substring(path.indexOf(jar) + 6, path.indexOf(".properties"));
                        } else if (path.contains(rootPath)) {
                            return path.substring(path.indexOf(rootPath) + rootPath.length(), path.indexOf(".properties"));
                        } else {
                            return null;
                        }
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
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
