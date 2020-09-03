package cloud.agileframework.spring.util;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.LocaleResolver;

/**
 * @author 佟盟
 * 日期 2020/7/29 14:03
 * 描述 国际化工具
 * @version 1.0
 * @since 1.0
 */

public class MessageUtil {
    private static LocaleResolver localeResolver;
    private static MessageSource messageSource;

    private static void init() {
        localeResolver = BeanUtil.getBean(LocaleResolver.class);
        if (localeResolver == null) {
            throw new NoSuchBeanDefinitionException(LocaleResolver.class);
        }

        messageSource = BeanUtil.getBean(MessageSource.class);
        if (messageSource == null) {
            throw new NoSuchBeanDefinitionException(MessageSource.class);
        }
    }

    public static String message(String key, Object... params) {
        try {
            return messageRequire(key, params);
        } catch (Exception e) {
            return null;
        }
    }

    public static String messageRequire(String key, Object... params) {
        if (localeResolver == null || messageSource == null) {
            init();
        }
        return messageSource.getMessage(key, params, LocaleContextHolder.getLocale());
    }
}
