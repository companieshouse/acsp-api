package uk.gov.companieshouse.acsp.interceptor;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthenticationInterceptorTest {

    private AuthenticationInterceptor authenticationInterceptor;

    @BeforeEach
    void setUp(){
        authenticationInterceptor = new AuthenticationInterceptor();
    }

    @Test
    void preHandleWithoutHeadersReturns401() {
        final var request = new MockHttpServletRequest();
        final var response = new MockHttpServletResponse();

        assertFalse(authenticationInterceptor.preHandle(request, response, null));
        assertEquals(401, response.getStatus());
    }

    @Test
    void preHandleWithoutEricIdentityReturns401() {
        final var request = new MockHttpServletRequest();
        final var response = new MockHttpServletResponse();
        request.addHeader("Eric-Identity-Type", "oauth2");

        assertFalse( authenticationInterceptor.preHandle(request, response, null ) );
        assertEquals( 401, response.getStatus() );
    }

    @Test
    void preHandleWithoutEricIdentityTypeReturns401() {
        final var request = new MockHttpServletRequest();
        request.addHeader("Eric-Identity", "abcd123456");

        final var response = new MockHttpServletResponse();
        assertFalse(authenticationInterceptor.preHandle( request, response, null ) );
        assertEquals( 401, response.getStatus() );
    }

    @Test
    void preHandleWithIncorrectEricIdentityTypeReturns401() {
        final var request = new MockHttpServletRequest();
        request.addHeader("Eric-Identity", "abcd123456");
        request.addHeader("Eric-Identity-Type", "key");

        final var response = new MockHttpServletResponse();
        assertFalse( authenticationInterceptor.preHandle(request, response, null ) );
        assertEquals( 401, response.getStatus() );
    }

    @Test
    void preHandleShouldReturnTrueWhenAuthHeaderAndAuthHeaderTypeOauthAreProvided() {
        final var request = new MockHttpServletRequest();
        request.addHeader("Eric-identity", "111");
        request.addHeader("Eric-identity-type", "oauth2");

        final var response = new MockHttpServletResponse();
        assertTrue( authenticationInterceptor.preHandle(request, response, null ) );
    }
}
