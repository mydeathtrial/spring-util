package cloud.agileframework.spring.util;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

/**
 * @author 佟盟
 * 日期 2020/7/29 14:36
 * 描述 提供上下文容器
 * @version 1.0
 * @since 1.0
 */
public class BeanUtil {
    private BeanUtil() {
    }

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        BeanUtil.applicationContext = applicationContext;
    }

    /**
     * 取上下文容器
     *
     * @return 容器
     */
    public static ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            applicationContext = ContextLoader.getCurrentWebApplicationContext();
        }
        return applicationContext;
    }

    /**
     * 根据bean名获取bean对象
     *
     * @param clazz bean类型
     * @return bean对象
     */
    public static <T> T getBean(Class<T> clazz) {
        try {
            return applicationContext.getBean(clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据bean名获取bean对象
     *
     * @param clazz bean名字
     * @return bean对象
     */
    public static Object getBean(String clazz) {
        try {
            return applicationContext.getBean(clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取bean的真实类型
     *
     * @param bean bean
     * @return 真实类型
     */
    public static Class<?> getBeanClass(Object bean) {
        if (AopUtils.isAopProxy(bean)) {
            return AopProxyUtils.ultimateTargetClass(bean);
        }
        return bean.getClass();
    }
}
