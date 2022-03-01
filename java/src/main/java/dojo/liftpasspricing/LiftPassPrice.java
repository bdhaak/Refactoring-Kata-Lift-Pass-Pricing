package dojo.liftpasspricing;

import java.util.Objects;

public class LiftPassPrice {

    public static final LiftPassPrice FREE = new LiftPassPrice(0);

    private Integer price;

    public LiftPassPrice(Integer price) {
        this.price = price;
    }

    public Integer getCost() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiftPassPrice that = (LiftPassPrice) o;
        return Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price);
    }

    @Override
    public String toString() {
        return "LiftPassPrice{" +
                "price=" + price +
                '}';
    }
}
