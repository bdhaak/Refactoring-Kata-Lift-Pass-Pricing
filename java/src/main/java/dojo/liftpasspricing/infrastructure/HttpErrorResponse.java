package dojo.liftpasspricing.infrastructure;

public class HttpErrorResponse implements LiftServerResponse {
    private final Throwable throwable;

    public HttpErrorResponse(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public int statusCode() {
        return 400;
    }

    public String toJSON(){
        return String.format("{ \"error\": \"%s\"}", throwable.getMessage());
    }
}
