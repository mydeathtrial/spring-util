package cloud.agileframework.spring.util;

import cloud.agileframework.common.constant.Constant;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
}
