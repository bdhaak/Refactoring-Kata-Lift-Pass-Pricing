package dojo.liftpasspricing.infrastructure;

import dojo.liftpasspricing.infrastructure.LiftPassServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("Disable and make the test below pass")
class LiftPassServerWithoutMocksTest {

    LiftPassServer liftPassServer;

    @BeforeEach
    void setUp() {
        liftPassServer = LiftPassServer.nullable();
        // Hint: Look at LiftPassResponse
        liftPassServer.putPrice(null); // TODO: Fix me first
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
        // hint: req.params()
        String response = liftPassServer.getPrice(null); // TODO: Fix me third
        assertEquals("{ \"cost\": 100}", response);
    }
}