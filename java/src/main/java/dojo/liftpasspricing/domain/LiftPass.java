package dojo.liftpasspricing.domain;

public final class LiftPass {

    private final int cost;
    private final LiftPassType type;

    public LiftPass(int cost, String type) {
        this.cost = cost;
        this.type = LiftPassType.fromValue(type);
    }

    public LiftPassType getType() {
        return type;
    }

    public int getCost() {
        return cost;
    }

    public boolean isNightType() {
        return type.equals(LiftPassType.NIGHT);
    }
}
