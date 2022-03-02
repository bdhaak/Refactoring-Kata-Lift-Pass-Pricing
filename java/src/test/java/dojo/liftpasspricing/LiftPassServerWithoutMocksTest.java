package dojo.liftpasspricing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spark.Request;
import spark.RequestResponseFactory;
import spark.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@Disabled("Disable and make the test below pass")
class LiftPassServerWithoutMocksTest {

    LiftPassServer liftPassServer;

    @BeforeEach
    void setUp() {
        liftPassServer = LiftPassServer.nullable();
        // Hint: Look at LiftPassResponse
        liftPassServer.putPrice(null, null); // TODO: Fix me first
    }

    @Test
    void should_return_error_message() {
        String response = liftPassServer.getPrice(null); // TODO: Fix me second
        assertEquals("{ \"error\": \"Invalid customer age\"}", response);
    }

    @Test
    void should_return_full_price_when_age_is_18() {
        // age = 18
        // type=1hour
        // date=empty
        String response = liftPassServer.getPrice(null); // TODO: Fix me third
        assertEquals("{ \"cost\": 100}", response);
    }
}