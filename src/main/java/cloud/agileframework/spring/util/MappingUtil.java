package cloud.agileframework.spring.util;

import cloud.agileframework.spring.util.spring.BeanUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.handler.MatchableHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author 佟盟
 * 日期 2020/8/00021 18:28
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class MappingUtil {
    private static final String HANDLE_METHOD = "$cloud.agileframework.handlerMethod";

    /**
     * 根据请求，提取请求的目标方法
     *
     * @param request 请求
     * @return 目标方法标志
     */
    public static HandlerMethod matching(HttpServletRequest request) {
        HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute(HANDLE_METHOD);
        if (handlerMethod != null) {
            return handlerMethod;
        }

        ObjectProvider<MatchableHandlerMapping> handlerMappings = BeanUtil.getApplicationContext().getBeanProvider(MatchableHandlerMapping.class);

        handlerMethod = handlerMappings.orderedStream().map(handlerMapping -> {
            try {
                HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);
                Object handler;
                if (handlerExecutionChain != null) {
                    handler = handlerExecutionChain.getHandler();
                    if (handler instanceof HandlerMethod) {
                        return (HandlerMethod) handler;
                    }
                }
            } catch (Exception ignored) {
            }
            return null;
        }).filter(Objects::nonNull).findFirst().orElse(null);
        request.setAttribute(HANDLE_METHOD, handlerMethod);
        return handlerMethod;
    }
}
