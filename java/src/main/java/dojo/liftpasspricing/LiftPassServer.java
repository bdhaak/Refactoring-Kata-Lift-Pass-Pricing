package dojo.liftpasspricing;

import spark.Spark;

import static java.lang.Thread.sleep;
import static spark.Spark.*;

public class LiftPassServer {

    public static final int SERVER_PORT = 4567;
    public static final String APPLICATION_JSON = "application/json";

    public void start(int port) {

        System.out.println("Starting Server");

        int assignedServerPort = port == 0 ? SERVER_PORT : port;
        Spark.port(assignedServerPort);

        put("/prices", (req, res) -> {
            int liftPassCost = Integer.parseInt(req.queryParams("cost"));
            String liftPassType = req.queryParams("type");

            LiftPass liftPass = new LiftPass(liftPassCost, liftPassType);
            new LiftPassService().add(liftPass);

            res.status(200);
            return PriceResponse.EMPTY;
        });

        get("/prices", (req, res) -> {
            Integer age = req.queryParams("age") != null ? Integer.valueOf(req.queryParams("age")) : null;
            String type = req.queryParams("type");
            String date = req.queryParams("date");

            LiftPassPrice price;
            try {
                price = new LiftPassService().getLiftPassPrice(new CustomerAge(age), type, date);
            } catch (InvalidCustomerAgeException e) {
                return new ErrorResponse(e).toJSON();
            }
            return new PriceResponse(price).toJSON();
        });

        after((req, res) -> {
            res.type(APPLICATION_JSON);
        });

        System.out.println(String.format("LiftPassPricing Api started on %d", assignedServerPort));
        System.out.println(String.format("you can open http://localhost:%d/prices?type=night&age=23&date=2019-02-18 in a navigator\n"
                + "and you'll get the price of the list pass for the day.", assignedServerPort));
    }

    public void stop() {
        System.out.println("Stopping Server");
        Spark.stop();
        try {
            sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class PriceResponse{
        private final LiftPassPrice liftPassPrice;

        public static String EMPTY = "";

        public PriceResponse(LiftPassPrice liftPassPrice) {
            this.liftPassPrice = liftPassPrice;
        }

        public String toJSON(){
            return String.format("{ \"cost\": %d}", liftPassPrice.getCost());
        }
    }

    static class ErrorResponse{
        private final Throwable throwable;
        public ErrorResponse(Throwable throwable) {
            this.throwable = throwable;
        }
        public String toJSON(){
            return String.format("{ \"error\": \"%s\"}", throwable.getMessage());
        }
    }

}
