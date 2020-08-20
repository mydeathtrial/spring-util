package cloud.agileframework.spring.util;

import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.common.util.clazz.TypeReference;
import cloud.agileframework.common.util.json.JSONUtil;
import cloud.agileframework.common.util.object.ObjectUtil;
import cloud.agileframework.spring.util.spring.MultipartFileUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 佟盟
 * 日期 2019/4/12 14:10
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class ParamUtil {

    /**
     * 根据servlet请求、认证信息、目标服务名、目标方法名处理入参
     */
    public static Map<String, Object> handleInParam(HttpServletRequest currentRequest) {
        final int length = 16;
        Map<String, Object> inParam = new HashMap<>(length);
        if (currentRequest == null) {
            return inParam;
        }

        inParam = parseOrdinaryVariable(currentRequest);

        //将处理过的所有请求参数传入调用服务对象
        return inParam;
    }

    /**
     * 合并两个map结构，相同key时value合并成数组
     *
     * @param from 从
     * @param to   到
     */
    private static void combine(Map<String, Object> from, Map<String, Object> to) {
        to.forEach((key, value) -> {
            Object old = from.get(key);
            if (old == null) {
                from.put(key, value);
            } else {
                if (Collection.class.isAssignableFrom(old.getClass())) {
                    ((Collection<Object>) old).add(value);
                } else if (old.getClass().isArray()) {
                    Object[] temp = new Object[Array.getLength(old) + 1];
                    for (int i = 0; i < Array.getLength(old); i++) {
                        temp[i] = Array.get(old, i);
                    }
                    temp[Array.getLength(old)] = value;
                    from.put(key, temp);
                } else {
                    Object[] temp = new Object[2];
                    temp[0] = old;
                    temp[1] = value;
                    from.put(key, temp);
                }
            }
        });
    }

    public static Map<String, Object> parseOrdinaryVariable(HttpServletRequest currentRequest) {
        Map<String, Object> inParam = Maps.newHashMap();
        Map<String, String[]> parameterMap = currentRequest.getParameterMap();
        if (parameterMap.size() > 0) {
            for (Map.Entry<String, String[]> map : parameterMap.entrySet()) {
                String[] v = map.getValue();
                if (v.length == 1) {
                    inParam.put(map.getKey(), v[0]);
                } else {
                    inParam.put(map.getKey(), v);
                }
            }
        }

        if (currentRequest instanceof RequestWrapper) {
            Map<String, String[]> forwardMap = currentRequest.getParameterMap();
            for (Map.Entry<String, String[]> map : forwardMap.entrySet()) {
                inParam.put(map.getKey(), map.getValue());
            }
        }

        //判断是否存在文件上传
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(currentRequest.getSession().getServletContext());
        if (multipartResolver.isMultipart(currentRequest)) {
            Map<String, Object> formData = MultipartFileUtil.getFileFormRequest(currentRequest);

            for (Map.Entry<String, Object> entry : formData.entrySet()) {
                if (inParam.containsKey(entry.getKey())) {
                    continue;
                }
                inParam.put(entry.getKey(), entry.getValue());
            }
        } else {
            String bodyParam = ServletUtil.getBody(currentRequest);
            if (bodyParam != null) {
                inParam.put(Constant.ResponseAbout.BODY, bodyParam);
            }
        }

        Enumeration<String> attributeNames = currentRequest.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String key = attributeNames.nextElement();
            String prefix = "Parameter_";
            if (key.startsWith(prefix)) {
                inParam.put(key.replace(prefix, ""), currentRequest.getAttribute(key));
            }
        }
        return coverToMap(inParam);
    }

    /**
     * 根据path key获取参数
     *
     * @param map 参数集合
     * @param key path key
     * @return 参数
     */
    public static Object getInParam(Map<String, Object> map, String key) {
        return JSONUtil.pathGet(key, map);
    }

    /**
     * 根据path key获取参数,取空时返回默认值
     *
     * @param map          参数集合
     * @param key          path key
     * @param defaultValue 默认值
     * @return 参数
     */
    public static <T> T getInParam(Map<String, Object> map, String key, T defaultValue) {
        Object value = getInParam(map, key);
        if (value != null) {
            return ObjectUtil.to(value, new TypeReference<>(defaultValue.getClass()));
        } else {
            return defaultValue;
        }
    }

    /**
     * 根据path key获取参数,并转换成指定类型，取空时返回默认值
     *
     * @param map          参数集合
     * @param key          path key
     * @param clazz        指定转换的类型
     * @param defaultValue 默认值
     * @param <T>          泛型
     * @return 参数
     */
    public static <T> T getInParam(Map<String, Object> map, String key, Class<T> clazz, T defaultValue) {
        T value = getInParam(map, key, clazz);
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }

    /**
     * 根据path key获取参数,并转换成指定类型，取空时返回默认值
     *
     * @param map           参数集合
     * @param key           path key
     * @param typeReference 指定转换的类型
     * @param <T>           泛型
     * @return 参数
     */
    public static <T> T getInParam(Map<String, Object> map, String key, TypeReference<T> typeReference) {
        return getInParam(map, key, typeReference, null);
    }

    /**
     * 根据path key获取参数,并转换成指定类型，取空时返回默认值
     *
     * @param map           参数集合
     * @param key           path key
     * @param typeReference 指定转换的类型
     * @param defaultValue  默认值
     * @param <T>           泛型
     * @return 参数
     */
    public static <T> T getInParam(Map<String, Object> map, String key, TypeReference<T> typeReference, T defaultValue) {

        Object value = getInParam(map, key);

        if (value != null) {
            T v = ObjectUtil.to(value, typeReference);
            return v == null ? defaultValue : v;
        } else {
            return defaultValue;
        }
    }

    /**
     * 入参里获取上传文件
     *
     * @param map 入参集和
     * @param key key值
     * @return 文件
     */
    public static MultipartFile getInParamOfFile(Map<String, Object> map, String key) {
        List<MultipartFile> files = getInParamOfFiles(map, key);
        if (!files.isEmpty()) {
            return files.get(0);
        }
        return null;
    }

    /**
     * 入参里获取上传文件
     *
     * @param map 入参集和
     * @param key key值
     * @return 文件
     */
    public static List<MultipartFile> getInParamOfFiles(Map<String, Object> map, String key) {
        return (List<MultipartFile>) getInParam(map, key);
    }

    /**
     * 判断path key是否存在
     *
     * @param map 参数集合
     * @param key path key
     * @return 是否存在
     */
    public static boolean containsKey(Map<String, Object> map, String key) {
        Object value = getInParam(map, key);
        return value != null;
    }

    /**
     * 入参转换成map嵌套map/list
     *
     * @param map 未处理的入参集合
     * @return 处理过后的入参集合
     */
    private static Map<String, Object> coverToMap(Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>(map);

        Object body = map.get(Constant.ResponseAbout.BODY);
        if (body != null) {
            try {
                Object json = JSONUtil.toMapOrList(JSONUtil.toJSON(body.toString()));
                if (json == null) {
                    return null;
                }
                if (Map.class.isAssignableFrom(json.getClass())) {
                    combine(result, (Map<String, Object>) json);
                    result.remove(Constant.ResponseAbout.BODY);
                } else if (List.class.isAssignableFrom(json.getClass())) {
                    result.put(Constant.ResponseAbout.BODY, json);
                }
            } catch (Exception e) {
                result.put(Constant.ResponseAbout.BODY, body);
            }
        } else {
            result.remove(Constant.ResponseAbout.BODY);
        }
        return result;
    }

    /**
     * 整个入参集转换成指定类型对象
     *
     * @param map   入参集
     * @param clazz 类型
     * @param <T>   泛型
     * @return 参数集和转换后的对象
     */
    public static <T> T getInParam(Map<String, Object> map, Class<T> clazz) {
        T result = ObjectUtil.to(map, new TypeReference<T>(clazz) {
        });

        if (result == null || ObjectUtil.isAllNullValidity(result)) {
            return null;
        }
        return result;
    }

    public static <T> T getInParam(Map<String, Object> map, TypeReference<T> typeReference) {
        T result = ObjectUtil.to(map, typeReference);

        if (result == null || ObjectUtil.isAllNullValidity(result)) {
            return null;
        }
        return result;
    }

    public static <T> T getInParam(Map<String, Object> map, String key, Class<T> clazz) {
        return ObjectUtil.to(JSONUtil.pathGet(key, map), new TypeReference<>(clazz));
    }

    public static <T> List<T> getInParamOfArray(Map<String, Object> map, String key, Class<T> clazz) {
        Object value = JSONUtil.pathGet(key, map);
        return ObjectUtil.to(value, new TypeReference<>(clazz));
    }

    /**
     * 请求中获取header或cookies中的信息
     *
     * @param request 请求 请求
     * @param key     索引
     * @return 信息
     */
    public static String getInfo(HttpServletRequest request, String key) {
        String token = request.getHeader(key);

        if (StringUtils.isBlank(token)) {
            token = getCookie(request, key);

            if (token == null) {
                Map<String, Object> map = ParamUtil.handleInParam(request);
                Object tokenValue = map.get(key);
                if (tokenValue != null) {
                    return tokenValue.toString();
                }
            }
        }
        return token;
    }

    /**
     * 获取cookies信息
     *
     * @param request 请求
     * @param key     索引
     * @return 值
     */
    public static String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (key.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
