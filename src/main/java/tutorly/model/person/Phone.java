package tutorly.model.person;

import static java.util.Objects.requireNonNull;
import static tutorly.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {

    public static final int MIN_LENGTH = 3;
    public static final int MAX_LENGTH = 25;

    public static final String MESSAGE_CONSTRAINTS = "Phone numbers should not be blank. It should only contain "
            + "numbers, spaces, hyphens, and an optional country code prefix. "
            + "It should be between " + MIN_LENGTH + " to " + MAX_LENGTH + " characters long.";

    public static final String VALIDATION_REGEX = "(\\+\\d{1,3}( )?)?[\\d -]+";

    private static final Phone EMPTY_PHONE = new Phone();

    public final String value;

    /**
     * Constructs an empty {@code Phone} instance.
     */
    private Phone() {
        value = "";
    }

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = phone;
    }

    /**
     * Returns an empty Phone instance.
     */
    public static Phone empty() {
        return EMPTY_PHONE;
    }

    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidPhone(String test) {
        return test.matches(VALIDATION_REGEX) && test.length() >= MIN_LENGTH && test.length() <= MAX_LENGTH;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Phone otherPhone)) {
            return false;
        }

        return value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
