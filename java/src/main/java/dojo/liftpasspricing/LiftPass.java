package dojo.liftpasspricing;

public final class LiftPass {

    private static final String TYPE_NIGHT = "night";

    private final int cost;
    private final String type;

    public LiftPass(int cost, String type) {
        this.cost = cost;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public int getCost() {
        return cost;
    }

    public boolean isNightType() {
        return type.equals(TYPE_NIGHT);
    }
}
