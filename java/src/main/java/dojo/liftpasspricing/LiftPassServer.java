package dojo.liftpasspricing;

import dojo.liftpasspricing.domain.CustomerAge;
import dojo.liftpasspricing.domain.InvalidCustomerAgeException;
import dojo.liftpasspricing.domain.LiftPass;
import dojo.liftpasspricing.domain.LiftPassPrice;
import dojo.liftpasspricing.infrastructure.HttpErrorResponse;
import dojo.liftpasspricing.infrastructure.LiftServerResponse;
import dojo.liftpasspricing.infrastructure.HttpPriceResponse;
import dojo.liftpasspricing.service.LiftPassService;
import spark.Request;
import spark.Spark;

import static spark.Spark.*;

public final class LiftPassServer {

    public static final int DEFAULT_SERVER_PORT = 4567;
    public static final String APPLICATION_JSON = "application/json";

    private final LiftPassService liftPassService;

    public LiftPassServer() {
        this(new LiftPassService());
    }

    private LiftPassServer(LiftPassService liftPassService) {
        this.liftPassService = liftPassService;
    }

    public static LiftPassServer nullable() {
        return new LiftPassServer(LiftPassService.nullable());
    }

    public void start(int port) {
        int assignedServerPort = port == 0 ? DEFAULT_SERVER_PORT : port;
        log("Starting Server");

        configureServerPort(assignedServerPort);
        configureHttpRoutes();

        logf(">>> LiftPassPricing Api started on %d%n", assignedServerPort);
        logf("you can open http://localhost:%d/prices?type=night&age=23&date=2019-02-18 in a navigator\n"
                + "and you'll get the price of the list pass for the day.%n", assignedServerPort);
    }

    private void configureServerPort(int assignedServerPort) {
        Spark.port(assignedServerPort);
    }

    private void configureHttpRoutes() {
        put("/prices", (req, res) -> {
            LiftServerResponse response = putPrice(req);
            res.status(response.statusCode());
            return response;
        });
        get("/prices", (req, res) -> {
            String response = getPrice(req);
            return response;
        });
        after((req, res) -> res.type(APPLICATION_JSON));
    }

    public String getPrice(Request req) {
        Integer age = req.queryParams("age") != null ? Integer.valueOf(req.queryParams("age")) : null;
        String type = req.queryParams("type");
        String date = req.queryParams("date");

        LiftServerResponse response;
        try {
            LiftPassPrice price = liftPassService.getLiftPassPrice(new CustomerAge(age), type, date);
            response = new HttpPriceResponse(price);
        } catch (InvalidCustomerAgeException e) {
            response = new HttpErrorResponse(e);
        }
        return response.toJSON();
    }

    public LiftServerResponse putPrice(Request req) {
        int liftPassCost = Integer.parseInt(req.queryParams("cost"));
        String liftPassType = req.queryParams("type");

        LiftPass liftPass = new LiftPass(liftPassCost, liftPassType);
        liftPassService.add(liftPass);

        return LiftServerResponse.EMPTY;
    }

    public void stop() {
        log("Stopping Server");
        Spark.stop();
        Spark.awaitStop();
    }

    private void log(String logStatement) {
        System.out.println(logStatement);
    }

    private void logf(String logStatement, Object... args) {
        System.out.printf(logStatement, args);
    }
}
