package dojo.liftpasspricing.domain;

import java.util.Objects;

public class InvalidCustomerAgeException extends Exception {

    public InvalidCustomerAgeException(String invalidCustomerAge) {
        super(invalidCustomerAge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessage());
    }

    @Override
    public boolean equals(Object obj) {
        return Objects.equals(((Exception)obj).getMessage(), this.getMessage());
    }
}
