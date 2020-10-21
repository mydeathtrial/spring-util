package cloud.agileframework.spring.util;

import com.google.common.collect.Lists;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 佟盟
 * 日期 2020-10-21 12:04
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class SecurityUtil {
    private static final String AGILE_SECURITY = "$AGILE_SECURITY";
    private static final UserDetails ANONYMOUS = new User("anonymous", "", Lists.newArrayList());

    public static UserDetails currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            authentication = (Authentication) ServletUtil.getCurrentRequest().getAttribute(AGILE_SECURITY);
        }
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {

            return (UserDetails) authentication.getPrincipal();
        }
        return ANONYMOUS;
    }

    public static void setCurrentUser(HttpServletRequest request, Authentication currentAuthentication) {
        request.setAttribute(AGILE_SECURITY, currentAuthentication);
    }
}
