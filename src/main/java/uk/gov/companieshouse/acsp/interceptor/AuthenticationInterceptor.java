package uk.gov.companieshouse.acsp.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
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
        final var hasEricIdentity = Objects.nonNull( request.getHeader( "Eric-Identity" ) );
        final var hasEricIdentityType = Objects.nonNull( request.getHeader( "Eric-Identity-Type" ) );

        if (!hasEricIdentityType || !hasEricIdentity){
            LOGGER.debugRequest(request, "AuthenticationInterceptor error: no authorised identity or identity type", null);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        if (isOauth2User(request) || isApiKeyUser(request)) {
            return true;
        }

        LOGGER.debugRequest(request, "AuthenticationInterceptor error: user not authorised", null);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

    private boolean isOauth2User(HttpServletRequest request) {
        return AuthorisationUtil.isOauth2User(request);
    }
    private boolean isApiKeyUser(HttpServletRequest request) {
        return request.getHeader("Eric-Identity-Type").equals("key");
    }
}
