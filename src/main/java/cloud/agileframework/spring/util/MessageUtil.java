package cloud.agileframework.spring.util;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * @author 佟盟
 * 日期 2020/7/29 14:03
 * 描述 国际化工具
 * @version 1.0
 * @since 1.0
 */

public class MessageUtil {
    private static MessageSource messageSource;

    private static void init() {
        messageSource = BeanUtil.getBean(MessageSource.class);
        if (messageSource == null) {
            throw new NoSuchBeanDefinitionException(MessageSource.class);
        }
    }

    public static String message(String key) {
        try {
            return messageRequire(key, null);
        } catch (Exception e) {
            return null;
        }
    }

    public static String message(String key, String defaultValue, Object... params) {
        try {
            return messageRequire(key, defaultValue, params);
        } catch (Exception e) {
            return null;
        }
    }

    public static String messageRequire(String key, String defaultValue, Object... params) {
        if (messageSource == null) {
            init();
        }
        return messageSource.getMessage(key, params, defaultValue, LocaleContextHolder.getLocale());
    }
}
