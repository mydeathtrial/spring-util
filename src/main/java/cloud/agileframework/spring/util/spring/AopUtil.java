package cloud.agileframework.spring.util.spring;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Method;

/**
 * @author 佟盟
 * 日期 2019/5/7 11:44
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class AopUtil extends AopUtils {
    /**
     * 从切面中获取触发切面的service
     *
     * @param joinPoint 触发切面的切入点
     * @return 返回住service
     */
    public static <T> T getTarget(JoinPoint joinPoint, Class<T> clazz) {
        Object target = joinPoint.getTarget();
        if (target != null && clazz.isAssignableFrom(target.getClass())) {
            return (T) target;
        }
        return null;
    }

    public static Method getMethd(JoinPoint joinPoint) {
        try {
            String methodName = joinPoint.getSignature().getName();
            Class<?> targetClass = joinPoint.getTarget().getClass();
            Class[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();
            return targetClass.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
