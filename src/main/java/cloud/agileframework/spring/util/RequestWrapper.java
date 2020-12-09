package cloud.agileframework.spring.util;

import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.common.util.clazz.TypeReference;
import cloud.agileframework.common.util.object.ObjectUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author 佟盟 on 2018/3/26
 * HttpServletRequest扩展对象
 */
public class RequestWrapper extends ContentCachingRequestWrapper {
    /**
     * request的parameters
     */
    private final Map<String, String[]> parameters;
    /**
     * request的入参集
     */
    private final Map<String, Object> inParam;

    public RequestWrapper(HttpServletRequest request) {
        super(request);

        this.parameters = Maps.newHashMap();
        parameters.remove(Constant.RequestAbout.SERVICE);
        parameters.remove(Constant.RequestAbout.METHOD);
        parameters.putAll(request.getParameterMap());

        inParam = ParamUtil.handleInParamWithFile(this);
    }

    /**
     * 提取包装请求
     *
     * @param request 请求
     * @return 包装后的请求
     */
    public static RequestWrapper extract(HttpServletRequest request) {
        RequestWrapper r = WebUtils.getNativeRequest(request, RequestWrapper.class);
        if (r == null) {
            return new RequestWrapper(request);
        }
        return r;
    }

    public static boolean isWrapper(HttpServletRequest request) {
        return WebUtils.getNativeRequest(request, RequestWrapper.class) != null;
    }


    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters;
    }

    /**
     * 为request添加parameter参数
     *
     * @param key key值
     * @param o   value值
     */
    public void addParameter(String key, String o) {
        if (this.parameters.containsKey(key)) {
            String[] value = parameters.get(key);
            parameters.put(key, ArrayUtils.add(value, o));
        }
        this.parameters.put(key, new String[]{o});
    }

    public Map<String, Object> getInParam() {
        return inParam;
    }

    public void extendInParam(Map<String, Object> params) {
        inParam.putAll(params);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        byte[] bytes = this.getContentAsByteArray();
        if (bytes.length > 0) {
            return new ServletInputStream() {
                final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

                @Override
                public int read() {
                    return inputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {

                }
            };
        }
        return super.getInputStream();
    }

    public boolean containsKey(String key) {
        return ParamUtil.containsKey(getInParam(), key);
    }

    /**
     * 服务中调用该方法获取入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public Object getInParam(String key) {
        return ParamUtil.getInParam(getInParam(), key);
    }


    /**
     * 服务中调用该方法获取映射对象
     *
     * @param clazz 参数映射类型
     * @return 入参映射对象
     */
    public <T> T getInParam(Class<T> clazz) {
        return ParamUtil.getInParam(getInParam(), clazz);
    }

    /**
     * 服务中调用该方法获取映射对象
     *
     * @param typeReference 参数映射类型
     * @return 入参映射对象
     */
    public <T> T getInParam(TypeReference<T> typeReference) {
        return ParamUtil.getInParam(getInParam(), typeReference);
    }

    /**
     * 服务中调用该方法获取映射对象
     *
     * @param clazz  参数映射类型
     * @param prefix 筛选参数前缀
     * @return 入参映射对象
     */
    public <T> T getInParamByPrefix(Class<T> clazz, String prefix) {
        return ObjectUtil.getObjectFromMap(clazz, getInParam(), prefix);
    }

    /**
     * 服务中调用该方法获取映射对象
     *
     * @param clazz  参数映射类型
     * @param prefix 筛选参数前缀
     * @param suffix 筛选参数后缀
     * @return 入参映射对象
     */
    public <T> T getInParamByPrefixAndSuffix(Class<T> clazz, String prefix, String suffix) {
        return ObjectUtil.getObjectFromMap(clazz, getInParam(), prefix, suffix);
    }

    /**
     * 服务中调用该方法获取入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public String getInParam(String key, String defaultValue) {
        return ParamUtil.getInParam(getInParam(), key, defaultValue);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public <T> T getInParam(String key, Class<T> clazz) {
        return ParamUtil.getInParam(getInParam(), key, clazz);
    }

    /**
     * 取path下入参，转换为指定泛型
     *
     * @param key       参数path
     * @param reference 泛型
     * @param <T>       泛型
     * @return 转换后的入参
     */
    public <T> T getInParam(String key, TypeReference<T> reference) {
        return ParamUtil.getInParam(getInParam(), key, reference);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public <T> T getInParam(String key, Class<T> clazz, T defaultValue) {
        return ParamUtil.getInParam(getInParam(), key, clazz, defaultValue);
    }

    /**
     * 获取上传文件
     *
     * @param key key值
     * @return 文件
     */
    public MultipartFile getInParamOfFile(String key) {
        return ParamUtil.getInParamOfFile(getInParam(), key);
    }

    /**
     * 获取上传文件
     *
     * @param key key值
     * @return 文件
     */
    public List<MultipartFile> getInParamOfFiles(String key) {
        return ParamUtil.getInParamOfFiles(getInParam(), key);
    }

    /**
     * 服务中调用该方法获取字符串数组入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public List<String> getInParamOfArray(String key) {
        return getInParamOfArray(key, String.class);
    }

    /**
     * 服务中调用该方法获取指定类型入参
     *
     * @param key 入参索引字符串
     * @return 入参值
     */
    public <T> List<T> getInParamOfArray(String key, Class<T> clazz) {
        return ParamUtil.getInParamOfArray(getInParam(), key, clazz);
    }
}
