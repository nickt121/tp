package tutorly.model.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tutorly.testutil.Assert.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class TimeslotTest {
    private static final LocalDateTime VALID_START_TIME = LocalDateTime.of(2025, 3, 25, 10, 0);
    private static final LocalDateTime VALID_END_TIME = LocalDateTime.of(2025, 3, 25, 12, 0);

    @Test
    public void constructor_success() {
        Timeslot timeslot = new Timeslot(VALID_START_TIME, VALID_END_TIME);
        assertEquals(VALID_START_TIME, timeslot.getStartTime());
        assertEquals(VALID_END_TIME, timeslot.getEndTime());
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Timeslot(null, null));
    }

    @Test
    public void constructor_invalidTimeslot_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Timeslot.MESSAGE_END_BEFORE_START_DATETIME, () ->
                new Timeslot(VALID_END_TIME, VALID_START_TIME));
    }

    @Test
    public void constructor_sameTimeslot_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Timeslot.MESSAGE_END_BEFORE_START_DATETIME, () ->
                new Timeslot(VALID_START_TIME, VALID_START_TIME));
    }

    @Test
    public void isOverlapping() {
        Timeslot timeslot1 = new Timeslot(VALID_START_TIME, VALID_END_TIME);
        Timeslot timeslot2 = new Timeslot(VALID_START_TIME.minusHours(1), VALID_END_TIME.minusHours(1));
        Timeslot timeslot3 = new Timeslot(VALID_START_TIME.plusHours(1), VALID_END_TIME.plusHours(1));
        Timeslot timeslot4 = new Timeslot(VALID_START_TIME.plusMinutes(30), VALID_END_TIME.minusMinutes(30));
        Timeslot timeslot5 = new Timeslot(VALID_END_TIME, VALID_END_TIME.plusHours(2));
        Timeslot timeslot6 = new Timeslot(VALID_START_TIME.minusHours(2), VALID_START_TIME);

        assertTrue(timeslot1.isOverlapping(timeslot1)); // same timeslot
        assertTrue(timeslot1.isOverlapping(timeslot2));
        assertTrue(timeslot1.isOverlapping(timeslot3));
        assertTrue(timeslot1.isOverlapping(timeslot4));
        assertFalse(timeslot1.isOverlapping(timeslot5));
        assertFalse(timeslot1.isOverlapping(timeslot6));
    }

    @Test
    public void equals() {
        Timeslot timeslot = new Timeslot(VALID_START_TIME, VALID_END_TIME);

        // same values -> returns true
        assertEquals(timeslot, new Timeslot(VALID_START_TIME, VALID_END_TIME));

        // same object -> returns true
        assertEquals(timeslot, timeslot);

        // null -> returns false
        assertNotEquals(null, timeslot);

        // different types -> returns false
        assertFalse(timeslot.equals(5.0f));

        // different values -> returns false
        assertNotEquals(timeslot, new Timeslot(VALID_START_TIME, VALID_END_TIME.plusHours(1)));
    }

    @Test
    public void toStringMethod() {
        Timeslot timeslot = new Timeslot(VALID_START_TIME, VALID_END_TIME);
        String expected = Timeslot.class.getCanonicalName()
                + "{startTime=" + timeslot.getStartTime()
                + ", endTime=" + timeslot.getEndTime() + "}";
        assertEquals(expected, timeslot.toString());
    }
}
