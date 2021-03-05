package cloud.agileframework.spring.util;

import cloud.agileframework.common.util.stream.StreamUtil;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

/**
 * @author 佟盟 on 2017/2/23
 */
public class ServletUtil {
    public static String getCurrentRequestIP() {
        HttpServletRequest request = getCurrentRequest();
        return getRequestIP(request);
    }

    public static String getCurrentRequestUrl() {
        HttpServletRequest request = getCurrentRequest();
        return request.getRequestURI();
    }

    /**
     * 获取http请求的真实IP地址
     *
     * @param request 请求对象
     * @return 返回IP地址
     */
    public static String getRequestIP(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ip = request.getHeader(PropertiesUtil.getProperty("agile.security.real-ip-header", "X-Real-Ip"));
        final String unknown = "unknown";
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return localhostFormat(ip);
    }

    /**
     * 本地地址格式化
     *
     * @param ip IP地址
     * @return 格式化后的IP地址
     */
    public static String localhostFormat(String ip) {
        final String localhost = "127.0.0.1";
        final String localhost2 = "0:0:0:0:0:0:0:1";
        if (localhost.equals(ip) || localhost2.equals(ip)) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException unknownhostexception) {
                ip = "未知IP地址";
            }
        }
        return ip;
    }

    /**
     * 获取Linux下的IP地址
     *
     * @return IP地址
     */
    private static String getLinuxLocalIp() {
        String ip = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                String name = intf.getName();
                if (!name.contains("docker") && !name.contains("lo")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            String ipaddress = inetAddress.getHostAddress();
                            if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
                                ip = ipaddress;
                            }
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    /**
     * 获取本地Host名称
     */
    public static String getLocalHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断操作系统是否是Windows
     */
    private static boolean isWindowsOS() {
        boolean isWindowsOS = false;
        String osName = System.getProperty("os.name");
        final String windows = "windows";
        if (osName.toLowerCase().contains(windows)) {
            isWindowsOS = true;
        }
        return isWindowsOS;
    }

    /**
     * 获取本地IP地址
     */
    public static String getLocalIP() {
        if (isWindowsOS()) {
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                return "未知";
            }
        } else {
            return getLinuxLocalIp();
        }
    }

    /**
     * 处理body参数
     *
     * @param request 请求request
     */
    public static byte[] getBody(HttpServletRequest request) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            StreamUtil.toOutputStream(request.getInputStream(), bytes);
            return bytes.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前request中的请求地址
     *
     * @return url
     */
    public static String getCurrentUrl(HttpServletRequest request) {
        if (request != null) {
            return request.getRequestURL().toString();
        }
        return null;
    }

    /**
     * 获取当前请求
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        throw new RuntimeException("not found request");
    }

    /**
     * 获取当前请求
     *
     * @return HttpServletResponse
     */
    public static HttpServletResponse getCurrentResponse() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getResponse();
        }
        throw new RuntimeException("not found response");
    }

    /**
     * 获取当前请求
     *
     * @return SessionId
     */
    public static String getCurrentSessionId() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return requestAttributes.getSessionId();
        }
        return null;
    }

    /**
     * 字符串数组转RequestMatcher
     *
     * @param urls 预匹配路径
     * @return RequestMatcher集合
     */
    public static List<RequestMatcher> coverRequestMatcher(String... urls) {
        List<RequestMatcher> list = new ArrayList<>();
        for (String url : urls) {
            list.add(new AntPathRequestMatcher(url));
        }
        return list;
    }

    /**
     * 请求路径匹配
     *
     * @param request 请求
     * @param paths   预匹配路径
     * @return 是/否
     */
    public static boolean matcherRequest(HttpServletRequest request, String... paths) {
        for (String path : paths) {
            AntPathRequestMatcher matcher = new AntPathRequestMatcher(path);
            if (matcher.matches(request)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 请求路径匹配
     *
     * @param request         请求
     * @param requestMatchers 预匹配路径
     * @return 是/否
     */
    public static boolean matcherRequest(HttpServletRequest request, Collection<RequestMatcher> requestMatchers) {
        for (RequestMatcher requestMatcher : requestMatchers) {
            if (requestMatcher.matches(request)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取本机的所有ip
     *
     * @return
     */
    public static List<InetAddress> getLocalIpAddr() {
        List<InetAddress> ipList = new ArrayList<>();
        Enumeration<?> interfaces;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            return ipList;
        }
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) interfaces.nextElement();
            Enumeration<?> ipAddrEnum = ni.getInetAddresses();
            while (ipAddrEnum.hasMoreElements()) {
                InetAddress addr = (InetAddress) ipAddrEnum.nextElement();
                if (addr.isLoopbackAddress() || addr.isLinkLocalAddress()) {
                    continue;
                }
                ipList.add(addr);
            }
        }


        return ipList;
    }
}
