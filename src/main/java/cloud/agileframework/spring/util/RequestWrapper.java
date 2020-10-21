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
    private final Map<String, String[]> params;
    private Map<String, Object> inParam;

    public RequestWrapper(HttpServletRequest request) {
        super(request);

        this.params = Maps.newHashMap();
        params.remove(Constant.ResponseAbout.SERVICE);
        params.remove(Constant.ResponseAbout.METHOD);
        params.putAll(request.getParameterMap());
    }

    public static RequestWrapper of(HttpServletRequest request) {
        RequestWrapper r = WebUtils.getNativeRequest(request, RequestWrapper.class);
        if (r == null) {
            return new RequestWrapper(request);
        }
        return r;
    }


    @Override
    public Map<String, String[]> getParameterMap() {
        return params;
    }

    /**
     * 为request添加parameter参数
     *
     * @param key key值
     * @param o   value值
     */
    public void addParameter(String key, String o) {
        if (this.params.containsKey(key)) {
            String[] value = params.get(key);
            params.put(key, ArrayUtils.add(value, o));
        }
        this.params.put(key, new String[]{o});
    }

    public Map<String, Object> getInParam() {
        if (inParam == null) {
            inParam = Maps.newHashMap();
        }
        inParam.putAll(ParamUtil.handleInParam(this));
        return inParam;
    }

    public Map<String, Object> getInParamWithFile() {
        if (inParam == null) {
            inParam = Maps.newHashMap();
        }
        inParam.putAll(ParamUtil.handleInParamWithFile(this));
        return inParam;
    }

    public void extendInParam(Map<String, Object> params) {
        getInParam().putAll(params);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        byte[] bytes = this.getContentAsByteArray();
        if(bytes.length>0){
            return new ServletInputStream(){
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
