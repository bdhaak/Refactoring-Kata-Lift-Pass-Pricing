package dojo.liftpasspricing;

import spark.Request;
import spark.Response;
import spark.Spark;

import static spark.Spark.*;

public class LiftPassServer {

    public static final int SERVER_PORT = 4567;
    public static final String APPLICATION_JSON = "application/json";

    private final LiftPassService liftPassService;

    public LiftPassServer() {
        this.liftPassService = new LiftPassService(new MysqlLiftPassRepository());
    }

    private LiftPassServer(LiftPassService liftPassService) {
        this.liftPassService = liftPassService;
    }

    public static LiftPassServer nullable() {
        return new LiftPassServer(new LiftPassService(new InMemoryLiftPassRepository()));
    }

    public void start(int port) {

        System.out.println("Starting Server");

        int assignedServerPort = port == 0 ? SERVER_PORT : port;
        Spark.port(assignedServerPort);

        put("/prices", (req, res) -> putPrice(req, res));
        get("/prices", (req, res) -> getPrice(req));
        after((req, res) -> res.type(APPLICATION_JSON));

        System.out.printf(">>> LiftPassPricing Api started on %d%n", assignedServerPort);
        System.out.printf("you can open http://localhost:%d/prices?type=night&age=23&date=2019-02-18 in a navigator\n"
                + "and you'll get the price of the list pass for the day.%n", assignedServerPort);
    }

    public String getPrice(Request req) {
        Integer age = req.queryParams("age") != null ? Integer.valueOf(req.queryParams("age")) : null;
        String type = req.queryParams("type");
        String date = req.queryParams("date");

        LiftPassResponse response;
        try {
            LiftPassPrice price = liftPassService.getLiftPassPrice(new CustomerAge(age), type, date);
            response = new PriceResponse(price);
        } catch (InvalidCustomerAgeException e) {
            response = new ErrorResponse(e);
        }
        return response.toJSON();
    }

    public String putPrice(Request req, Response res) {
        int liftPassCost = Integer.parseInt(req.queryParams("cost"));
        String liftPassType = req.queryParams("type");

        LiftPass liftPass = new LiftPass(liftPassCost, liftPassType);
        liftPassService.add(liftPass);

        res.status(201);
        return PriceResponse.EMPTY;
    }

    public void stop() {
        System.out.println("Stopping Server");
        Spark.stop();
        Spark.awaitStop();
    }

    interface LiftPassResponse{
        String EMPTY = "";
        String toJSON();
    }

    static class PriceResponse implements LiftPassResponse{
        private final LiftPassPrice liftPassPrice;

        public PriceResponse(LiftPassPrice liftPassPrice) {
            this.liftPassPrice = liftPassPrice;
        }

        public String toJSON(){
            return String.format("{ \"cost\": %d}", liftPassPrice.getCost());
        }
    }

    static class ErrorResponse implements LiftPassResponse{
        private final Throwable throwable;

        public ErrorResponse(Throwable throwable) {
            this.throwable = throwable;
        }

        public String toJSON(){
            return String.format("{ \"error\": \"%s\"}", throwable.getMessage());
        }
    }

}
