package dojo.liftpasspricing.domain;

import java.util.stream.Stream;

public enum LiftPassType {

    NIGHT("night"),
    ONE_HOUR("1hour")
    ;

    private final String type;

    LiftPassType(String type) {
        this.type = type;
    }

    public static LiftPassType fromValue(String givenType) {
        return Stream.of(values())
                .filter(liftPassType -> liftPassType.type.equalsIgnoreCase(givenType))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return type;
    }
}
