package uk.gov.companieshouse.acsp.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.companieshouse.acsp.AcspApplication;
import uk.gov.companieshouse.api.util.security.AuthorisationUtil;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.Objects;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AcspApplication.APP_NAMESPACE);

    /**
     * Ensure requests are authenticated for a user
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return isOauth2User(request, response);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // Add post handle if needed.
    }

    private boolean isOauth2User(HttpServletRequest request, HttpServletResponse response) {
        final var hasEricIdentity = Objects.nonNull( request.getHeader( "Eric-Identity" ) );
        final var hasEricIdentityType = Objects.nonNull( request.getHeader( "Eric-Identity-Type" ) );
        if ( hasEricIdentity && hasEricIdentityType && AuthorisationUtil.isOauth2User(request) ) {
            return true;
        }

        LOGGER.debugRequest(request, "AuthenticationInterceptor error: user not authorised", null);
        response.setStatus(401);
        return false;
    }
}
