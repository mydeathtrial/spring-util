package cloud.agileframework.spring.util.spring;

import org.springframework.context.MessageSource;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * @author 佟盟
 * 日期 2020/7/29 14:03
 * 描述 国际化工具
 * @version 1.0
 * @since 1.0
 */

public class MessageUtil {
    private MessageUtil() {
    }

    public static String message(String key, Object... params) {
        Locale locale;

        LocaleResolver localeResolver = BeanUtil.getBean(LocaleResolver.class);
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest currentRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
            locale = localeResolver.resolveLocale(currentRequest);
        } else {
            locale = Locale.CHINA;
        }

        MessageSource messageSource = BeanUtil.getBean(MessageSource.class);
        return messageSource.getMessage(key, params, locale);
    }
}
