package dojo.liftpasspricing.infrastructure;

import dojo.liftpasspricing.domain.LiftPassPrice;

public class HttpPriceResponse implements LiftServerResponse {
    private final LiftPassPrice liftPassPrice;

    public HttpPriceResponse(LiftPassPrice liftPassPrice) {
        this.liftPassPrice = liftPassPrice;
    }

    @Override
    public int statusCode() {
        return 200;
    }

    public String toJSON(){
        return String.format("{ \"cost\": %d}", liftPassPrice.getCost());
    }
}
