package cloud.agileframework.spring.util;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * @author 佟盟 on 2018/8/22
 * @author 佟盟
 */
public class ViewUtil {
    private static List<ViewResolver> viewResolvers;
    private static Locale locale;
    private static RequestToViewNameTranslator viewNameTranslator;

    public static void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));

        ApplicationContext context = BeanUtil.getApplicationContext();

        if (locale == null) {
            initLocaleResolver(context, request);
        }
        if (viewResolvers == null) {
            initViewResolvers(context);
        }
        if (viewNameTranslator == null) {
            initRequestToViewNameTranslator(context);
        }

        response.setLocale(locale);
        String viewName = mv.getViewName();
        if (viewName == null) {
            viewName = getDefaultViewName(request);
            mv.setViewName(viewName);
        }
        View view = resolveViewName(viewName, mv.getModel(), locale, request);

        if (mv.getStatus() != null) {
            response.setStatus(mv.getStatus().value());
        }

        view.render(mv.getModel(), request, response);
    }

    private static View resolveViewName(String viewName, @Nullable Map<String, Object> model, Locale locale, HttpServletRequest request) throws Exception {

        if (viewResolvers != null) {

            for (ViewResolver viewResolver : viewResolvers) {
                View view = viewResolver.resolveViewName(viewName, locale);
                if (view != null) {
                    return view;
                }
            }
        }

        throw new RuntimeException("No views were found!");
    }

    private static void initLocaleResolver(ApplicationContext context, HttpServletRequest request) throws IOException {
        List<LocaleResolver> localeResolvers = getDefaultStrategies(context, LocaleResolver.class);
        locale = !localeResolvers.isEmpty() ? (localeResolvers.get(0)).resolveLocale(request) : request.getLocale();
    }

    private static void initViewResolvers(ApplicationContext context) throws IOException {
        viewResolvers = null;
        Map<String, ViewResolver> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, ViewResolver.class, true, false);
        if (!matchingBeans.isEmpty()) {
            viewResolvers = new LinkedList<>(matchingBeans.values());
            AnnotationAwareOrderComparator.sort(viewResolvers);
        }

        if (viewResolvers == null) {
            viewResolvers = getDefaultStrategies(context, ViewResolver.class);
        }
    }

    private static void initRequestToViewNameTranslator(ApplicationContext context) throws IOException {
        try {
            viewNameTranslator = context.getBean("viewNameTranslator", RequestToViewNameTranslator.class);
        } catch (NoSuchBeanDefinitionException var3) {
            viewNameTranslator = getDefaultStrategies(context, RequestToViewNameTranslator.class).get(0);
        }

    }

    private static <T> List<T> getDefaultStrategies(ApplicationContext context, Class<T> strategyInterface) throws IOException {
        String key = strategyInterface.getName();
        ClassPathResource resource = new ClassPathResource("DispatcherServlet.properties", DispatcherServlet.class);
        Properties defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
        String value = defaultStrategies.getProperty(key);
        if (value == null) {
            return new LinkedList<>();
        } else {

            String[] classNames = StringUtils.commaDelimitedListToStringArray(value);
            List<T> strategies = new ArrayList<>(classNames.length);

            for (String className : classNames) {
                try {
                    Class<?> clazz = ClassUtils.forName(className, DispatcherServlet.class.getClassLoader());
                    T strategy = (T) context.getAutowireCapableBeanFactory().createBean(clazz);
                    strategies.add(strategy);
                } catch (ClassNotFoundException var13) {
                    throw new BeanInitializationException("Could not find DispatcherServlet's default strategy class [" + className + "] for interface [" + key + "]", var13);
                } catch (LinkageError var14) {
                    throw new BeanInitializationException("Unresolvable class definition for DispatcherServlet's default strategy class [" + className + "] for interface [" + key + "]", var14);
                }
            }

            return strategies;

        }
    }

    @Nullable
    private static String getDefaultViewName(HttpServletRequest request) throws Exception {
        return viewNameTranslator != null ? viewNameTranslator.getViewName(request) : null;
    }
}
