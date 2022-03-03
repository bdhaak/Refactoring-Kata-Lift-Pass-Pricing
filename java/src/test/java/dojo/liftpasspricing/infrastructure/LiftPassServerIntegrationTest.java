package dojo.liftpasspricing.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dojo.liftpasspricing.infrastructure.LiftPassServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.net.ServerSocket;

@Tag("IntegrationTest")
public class LiftPassServerIntegrationTest {

    int serverPort;
    LiftPassServer liftPassServer = new LiftPassServer();

    @BeforeEach
    public void createPrices() throws IOException {
        serverPort = findFreePort();
        liftPassServer.start(serverPort);
        createPrice("1hour", 35);
        createPrice("night", 19);
    }

    public static int findFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private void createPrice(String type, int cost) {
        given().
                when().
                params("type", type, "cost", cost).
                put("/prices").
                then().
                assertThat().
                contentType("application/json").
                assertThat().
                statusCode(201);
    }

    @AfterEach
    public void stopApplication()  {
        liftPassServer.stop();
    }

    @Test
    public void defaultCost() {
        JsonPath json = obtainPrice("type", "1hour", "age", "25");
        int cost = json.get("cost");
        assertEquals(35, cost);
    }

    @ParameterizedTest
    @CsvSource({ "5, 0", //
            "6, 25", //
            "14, 25", //
            "15, 35", //
            "25, 35", //
            "64, 35", //
            "65, 27" })
    public void worksForAllAges(int age, int expectedCost) {
        JsonPath json = obtainPrice("type", "1hour", "age", Integer.toString(age));
        int cost = json.get("cost");
        assertEquals(expectedCost, cost);
    }

    @Test
    public void errorWhenNoAgeDefined() {
        JsonPath json = obtainPrice("type", "night");
        String error = json.get("error");
        assertEquals("Invalid customer age", error);
    }

    @ParameterizedTest
    @CsvSource({ "5, 0", //
            "6, 19", //
            "25, 19", //
            "64, 19", //
            "65, 8" })
    public void worksForNightPasses(int age, int expectedCost) {
        JsonPath json = obtainPrice("type", "night", "age", age);
        int cost = json.get("cost");
        assertEquals(expectedCost, cost);
    }

    @ParameterizedTest
    @CsvSource({ "15, '2019-02-22', 35", //
            "15, '2019-02-25', 35", // winter holiday
            "15, '2019-03-11', 23", //
            "65, '2019-03-11', 18" })
    public void worksForMondayDeals(int age, String date, int expectedCost) {
        JsonPath json = obtainPrice("type", "1hour", "age", age, "date", date);
        int cost = json.get("cost");
        assertEquals(expectedCost, cost);
    }

    @Test
    public void worksForMondayDealsIfAgeIs15AndNoHolidayNoMonday() {
        JsonPath json = obtainPrice("type", "1hour", "age", 15, "date", "2019-03-11");
        int cost = json.get("cost");
        assertEquals(23, cost);
    }

    @Test
    public void worksForMondayDealsIfAgeIs15AndHolidayAndMonday() {
        JsonPath json = obtainPrice("type", "1hour", "age", 15, "date", "2019-02-25");
        int cost = json.get("cost");
        assertEquals(35, cost);
    }

    // TODO 2-4, and 5, 6 day pass

    private RequestSpecification given() {
        return RestAssured.given().
                accept("application/json").
                port(serverPort);
    }

    private JsonPath obtainPrice(String paramName, Object paramValue, Object... otherParamPairs) {
        return given().
                when().
                params(paramName, paramValue, otherParamPairs).
                get("/prices").
                then().
                assertThat().
                contentType("application/json").
                assertThat().
                statusCode(200).
                extract().jsonPath();
    }

}
