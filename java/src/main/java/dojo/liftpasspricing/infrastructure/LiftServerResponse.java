package dojo.liftpasspricing.infrastructure;

public interface LiftServerResponse {
    LiftServerResponse EMPTY = new LiftServerResponse() {
        @Override
        public String toJSON() { return ""; }
        @Override
        public int statusCode() { return 201; }
    };

    String toJSON();
    int statusCode();
}
