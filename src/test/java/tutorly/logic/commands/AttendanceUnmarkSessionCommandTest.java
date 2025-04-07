package tutorly.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tutorly.testutil.Assert.assertThrows;
import static tutorly.testutil.TypicalAddressBook.ALICE;
import static tutorly.testutil.TypicalAddressBook.ELLE;
import static tutorly.testutil.TypicalAddressBook.ENGLISH_SESSION;
import static tutorly.testutil.TypicalAddressBook.MATH_SESSION;
import static tutorly.testutil.TypicalAddressBook.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import tutorly.logic.Messages;
import tutorly.logic.commands.exceptions.CommandException;
import tutorly.model.Model;
import tutorly.model.ModelManager;
import tutorly.model.UserPrefs;
import tutorly.model.attendancerecord.AttendanceRecord;
import tutorly.model.attendancerecord.Feedback;
import tutorly.model.person.Identity;

public class AttendanceUnmarkSessionCommandTest {
    private static final int INVALID_ID = 999;
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final Identity aliceIdentity = new Identity(ALICE.getId());
    private final Identity elleIdentity = new Identity(ELLE.getId());

    @Test
    public void constructor_nullArg_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AttendanceUnmarkSessionCommand(null, 1));
    }

    @Test
    public void execute_validRecord_unmarkSuccessful() throws Exception {
        CommandResult commandResult =
                new AttendanceUnmarkSessionCommand(aliceIdentity, MATH_SESSION.getId()).execute(model);
        assertEquals(String.format(AttendanceUnmarkSessionCommand.MESSAGE_SUCCESS, ALICE.getName().fullName,
                Messages.format(MATH_SESSION)), commandResult.getFeedbackToUser());

        AttendanceRecord expectedRecord = new AttendanceRecord(
                ALICE.getId(), MATH_SESSION.getId(), false, Feedback.empty());
        assertTrue(model.findAttendanceRecord(expectedRecord).map(record -> !record.getAttendance()).orElse(false));
    }

    @Test
    public void execute_invalidIdentity_throwsCommandException() {
        AttendanceUnmarkSessionCommand attendanceUnmarkSessionCommand =
                new AttendanceUnmarkSessionCommand(new Identity(INVALID_ID), ENGLISH_SESSION.getId());

        assertThrows(CommandException.class, Messages.MESSAGE_PERSON_NOT_FOUND, () ->
                attendanceUnmarkSessionCommand.execute(model));
    }

    @Test
    public void execute_invalidSessionId_throwsCommandException() {
        AttendanceUnmarkSessionCommand attendanceUnmarkSessionCommand =
                new AttendanceUnmarkSessionCommand(aliceIdentity, INVALID_ID);

        assertThrows(CommandException.class, Messages.MESSAGE_SESSION_NOT_FOUND, () ->
                attendanceUnmarkSessionCommand.execute(model));
    }

    @Test
    public void execute_alreadyUnmarked_throwsCommandException() {
        AttendanceUnmarkSessionCommand attendanceUnmarkSessionCommand =
                new AttendanceUnmarkSessionCommand(elleIdentity, MATH_SESSION.getId());

        assertThrows(CommandException.class, String.format(AttendanceUnmarkSessionCommand.MESSAGE_ALREADY_UNMARKED,
                ELLE.getName().fullName, Messages.format(MATH_SESSION)), () ->
                attendanceUnmarkSessionCommand.execute(model));
    }

    @Test
    public void execute_notEnrolled_throwsCommandException() {
        AttendanceUnmarkSessionCommand attendanceUnmarkSessionCommand =
                new AttendanceUnmarkSessionCommand(elleIdentity, ENGLISH_SESSION.getId());

        assertThrows(CommandException.class, String.format(AttendanceUnmarkSessionCommand.MESSAGE_RECORD_NOT_FOUND,
                ELLE.getName().fullName, Messages.format(ENGLISH_SESSION)), () ->
                attendanceUnmarkSessionCommand.execute(model));
    }

    @Test
    public void equals() {
        AttendanceUnmarkSessionCommand unmarkBensonMathCommand =
                new AttendanceUnmarkSessionCommand(elleIdentity, MATH_SESSION.getId());
        AttendanceUnmarkSessionCommand unmarkDanielMathCommand =
                new AttendanceUnmarkSessionCommand(aliceIdentity, MATH_SESSION.getId());
        AttendanceUnmarkSessionCommand unmarkBensonEnglishCommand =
                new AttendanceUnmarkSessionCommand(elleIdentity, ENGLISH_SESSION.getId());

        // same object -> returns true
        assertEquals(unmarkBensonMathCommand, unmarkBensonMathCommand);

        // same values -> returns true
        AttendanceUnmarkSessionCommand markBensonMathCommandCopy =
                new AttendanceUnmarkSessionCommand(elleIdentity, MATH_SESSION.getId());
        assertEquals(unmarkBensonMathCommand, markBensonMathCommandCopy);

        // different types -> returns false
        assertNotEquals(1, unmarkBensonMathCommand);

        // null -> returns false
        assertNotEquals(null, unmarkBensonMathCommand);

        // different person -> returns false
        assertNotEquals(unmarkBensonMathCommand, unmarkDanielMathCommand);

        // different session -> returns false
        assertNotEquals(unmarkBensonMathCommand, unmarkBensonEnglishCommand);
    }

    @Test
    public void toStringMethod() {
        AttendanceUnmarkSessionCommand attendanceUnmarkSessionCommand =
                new AttendanceUnmarkSessionCommand(elleIdentity, MATH_SESSION.getId());
        String expected = AttendanceUnmarkSessionCommand.class.getCanonicalName()
                + "{identity=" + elleIdentity + ", sessionId=" + MATH_SESSION.getId() + "}";
        assertEquals(expected, attendanceUnmarkSessionCommand.toString());
    }

}
