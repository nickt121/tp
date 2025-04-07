package tutorly.model.person;

import java.util.Objects;

import tutorly.commons.util.ToStringBuilder;

/**
 * Represents a student's identity.
 * Since a student's ID and Name are unique in the address book, they can be used to identify a student.
 */
public class Identity {

    public static final String MESSAGE_INVALID_IDENTITY =
            "STUDENT_IDENTIFIER provided is not a valid student ID or name.";
    public static final String MESSAGE_INVALID_ID = "Student ID must be a positive integer.";
    public static final int UNKNOWN_ID = Integer.MAX_VALUE;

    private int id;
    private Name name;

    /**
     * Creates an identity with the given name.
     *
     * @param name The name of the student.
     */
    public Identity(Name name) {
        this.name = name;
    }

    /**
     * Creates an identity with the given ID.
     *
     * @param id The ID of the student.
     */
    public Identity(int id) {
        if (id < 1) {
            throw new IllegalArgumentException(MESSAGE_INVALID_ID);
        }

        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
     * Checks if the student ID is provided in the identity.
     *
     * @return True if the ID is present in the identity, false otherwise.
     */
    public boolean isIdPresent() {
        return id != 0;
    }

    public Name getName() {
        return name;
    }

    /**
     * Checks if the student name is provided in the identity.
     *
     * @return True if the name is present in the identity, false otherwise.
     */
    public boolean isNamePresent() {
        return name != null;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Identity otherIdentity)) {
            return false;
        }

        return id == otherIdentity.id && Objects.equals(name, otherIdentity.name);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("id", id)
                .add("name", name)
                .toString();
    }
}
