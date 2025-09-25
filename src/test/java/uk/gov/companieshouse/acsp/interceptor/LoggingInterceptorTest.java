package uk.gov.companieshouse.acsp.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.companieshouse.logging.util.LogContextProperties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoggingInterceptorTest {
  private static final String TEST_REQUEST_PATH = "/";

  @Mock
  private Object handler;
  @Mock
  private ModelAndView modelAndView;
  @Mock
  private HttpServletRequest mockRequest;
  @Mock
  private HttpServletResponse mockResponse;
  @Mock
  private HttpSession requestSession;
  private LoggingInterceptor loggingInterceptor;

  @BeforeEach
  void setUp() {
    when(mockRequest.getSession()).thenReturn(requestSession);
    when(mockRequest.getRequestURI()).thenReturn(TEST_REQUEST_PATH);
    loggingInterceptor = new LoggingInterceptor();
  }

  @Test
  void verifyLoggingPreHandle() {
    var response = loggingInterceptor.preHandle(mockRequest, mockResponse, handler);
    verify(mockRequest).getSession();
    verify(requestSession).setAttribute(anyString(), anyLong());
    assertThat(response, is(true));
  }

  @Test
  void verifyLoggingPostHandle() {
    long startTime = System.currentTimeMillis();
    when(mockRequest.getSession().getAttribute(LogContextProperties.START_TIME_KEY.value()))
        .thenReturn(startTime);
    loggingInterceptor.postHandle(mockRequest, mockResponse, handler, modelAndView);
    verify(mockResponse).getStatus();
  }

}
