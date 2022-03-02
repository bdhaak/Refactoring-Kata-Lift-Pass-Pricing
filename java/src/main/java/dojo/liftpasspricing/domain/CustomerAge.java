package dojo.liftpasspricing.domain;

public final class CustomerAge {

    private final Integer age;

    public CustomerAge(Integer age) {
        this.age = age;
    }

    public boolean isUnderSixYears() {
        return age != null && age < 6;
    }

    public boolean isOlderThanSixtyFour() {
        return age != null && age > 64;
    }

    public boolean isUnderFifteen() {
        return age != null && age < 15;
    }

    public boolean isInvalid() {
        return age == null || age < 0;
    }
}
