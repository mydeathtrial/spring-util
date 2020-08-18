package cloud.agileframework.spring.util.spring;

import org.springframework.core.env.Environment;

/**
 * @author 佟盟
 * 日期 2020/8/00014 18:39
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class PropertiesUtil extends cloud.agileframework.common.util.properties.PropertiesUtil {
    private static Environment environment;

    public static void setEnvironment(Environment environment) {
        PropertiesUtil.environment = environment;
    }

    public static String getProperty(String key) {
        String v = environment.getProperty(key);
        if (v == null) {
            return getProperties().getProperty(key);
        }
        return v;
    }

}
