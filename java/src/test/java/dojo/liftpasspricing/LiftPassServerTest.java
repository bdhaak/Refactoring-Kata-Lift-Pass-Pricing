package dojo.liftpasspricing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spark.Request;
import spark.RequestResponseFactory;
import spark.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LiftPassServerTest {

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    private LiftPassServer liftPassServer;

    @BeforeEach
    void setUp() {
        liftPassServer = LiftPassServer.nullable();

        when(httpServletRequest.getParameter("cost")).thenReturn("100");
        when(httpServletRequest.getParameter("type")).thenReturn("1hour");
        Request request = RequestResponseFactory.create(httpServletRequest);
        Response response = RequestResponseFactory.create(httpServletResponse);
        liftPassServer.putPrice(request, response);
    }

    @Test
    void should_return_error_message() {
        // Arrange
        Request request = RequestResponseFactory.create(httpServletRequest);

        // Act
        String response = liftPassServer.getPrice(request);

        // Assert
        assertEquals("{ \"error\": \"Invalid customer age\"}", response);
    }

    @Test
    void should_return_full_price_when_age_is_18() {
        // Arrange
        when(httpServletRequest.getParameter("age")).thenReturn("18");
        when(httpServletRequest.getParameter("type")).thenReturn("1hour");
        Request request = RequestResponseFactory.create(httpServletRequest);

        // Act
        String response = liftPassServer.getPrice(request);

        // Assert
        assertEquals("{ \"cost\": 100}", response);
    }
}