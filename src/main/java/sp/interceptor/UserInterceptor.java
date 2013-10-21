package sp.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Active User interceptor. Implements {@link HandlerInterceptor}
 *
 * @author Paul Kulitski
 * @see HandlerInterceptor
 */
public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    /**
     * Populate model with an active user details object
     *
     * @param request incoming request
     * @param response oncoming response
     * @param handler handler
     * @param modelAndView model and view instance
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            Object user = SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            if (user != null) {                
                modelAndView.addObject("user", user);
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
