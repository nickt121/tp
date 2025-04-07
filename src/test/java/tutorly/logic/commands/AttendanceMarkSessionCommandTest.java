package tutorly.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tutorly.testutil.Assert.assertThrows;
import static tutorly.testutil.TypicalAddressBook.BENSON;
import static tutorly.testutil.TypicalAddressBook.DANIEL;
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

public class AttendanceMarkSessionCommandTest {
    private static final int INVALID_ID = 999;
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final Identity bensonIdentity = new Identity(BENSON.getId());
    private final Identity danielIdentity = new Identity(DANIEL.getId());

    @Test
    public void constructor_nullArg_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AttendanceMarkSessionCommand(null, 1));
    }

    @Test
    public void execute_validRecord_markSuccessful() throws Exception {
        CommandResult commandResult =
                new AttendanceMarkSessionCommand(danielIdentity, MATH_SESSION.getId()).execute(model);
        assertEquals(String.format(AttendanceMarkSessionCommand.MESSAGE_SUCCESS, DANIEL.getName().fullName,
                Messages.format(MATH_SESSION)), commandResult.getFeedbackToUser());

        AttendanceRecord expectedRecord = new AttendanceRecord(
                DANIEL.getId(), MATH_SESSION.getId(), true, Feedback.empty());
        assertTrue(model.findAttendanceRecord(expectedRecord).map(record -> record.getAttendance()).orElse(false));
    }

    @Test
    public void execute_invalidIdentity_throwsCommandException() {
        AttendanceMarkSessionCommand attendanceMarkSessionCommand =
                new AttendanceMarkSessionCommand(new Identity(INVALID_ID), ENGLISH_SESSION.getId());

        assertThrows(CommandException.class, Messages.MESSAGE_PERSON_NOT_FOUND, () ->
                attendanceMarkSessionCommand.execute(model));
    }

    @Test
    public void execute_invalidSessionId_throwsCommandException() {
        AttendanceMarkSessionCommand attendanceMarkSessionCommand =
                new AttendanceMarkSessionCommand(danielIdentity, INVALID_ID);

        assertThrows(CommandException.class, Messages.MESSAGE_SESSION_NOT_FOUND, () ->
                attendanceMarkSessionCommand.execute(model));
    }

    @Test
    public void execute_alreadyMarked_throwsCommandException() {
        AttendanceMarkSessionCommand attendanceMarkSessionCommand =
                new AttendanceMarkSessionCommand(bensonIdentity, MATH_SESSION.getId());

        assertThrows(CommandException.class, String.format(AttendanceMarkSessionCommand.MESSAGE_ALREADY_MARKED,
                BENSON.getName().fullName, Messages.format(MATH_SESSION)), () ->
                attendanceMarkSessionCommand.execute(model));
    }

    @Test
    public void execute_notEnrolled_throwsCommandException() {
        AttendanceMarkSessionCommand attendanceMarkSessionCommand =
                new AttendanceMarkSessionCommand(bensonIdentity, ENGLISH_SESSION.getId());

        assertThrows(CommandException.class, String.format(AttendanceMarkSessionCommand.MESSAGE_RECORD_NOT_FOUND,
                BENSON.getName().fullName, Messages.format(ENGLISH_SESSION)), () ->
                attendanceMarkSessionCommand.execute(model));
    }

    @Test
    public void equals() {
        AttendanceMarkSessionCommand markBensonMathCommand =
                new AttendanceMarkSessionCommand(bensonIdentity, MATH_SESSION.getId());
        AttendanceMarkSessionCommand markDanielMathCommand =
                new AttendanceMarkSessionCommand(danielIdentity, MATH_SESSION.getId());
        AttendanceMarkSessionCommand markBensonEnglishCommand =
                new AttendanceMarkSessionCommand(bensonIdentity, ENGLISH_SESSION.getId());

        // same object -> returns true
        assertEquals(markBensonMathCommand, markBensonMathCommand);

        // same values -> returns true
        AttendanceMarkSessionCommand markBensonMathCommandCopy =
                new AttendanceMarkSessionCommand(bensonIdentity, MATH_SESSION.getId());
        assertEquals(markBensonMathCommand, markBensonMathCommandCopy);

        // different types -> returns false
        assertNotEquals(1, markBensonMathCommand);

        // null -> returns false
        assertNotEquals(null, markBensonMathCommand);

        // different person -> returns false
        assertNotEquals(markBensonMathCommand, markDanielMathCommand);

        // different session -> returns false
        assertNotEquals(markBensonMathCommand, markBensonEnglishCommand);
    }

    @Test
    public void toStringMethod() {
        AttendanceMarkSessionCommand attendanceMarkSessionCommand =
                new AttendanceMarkSessionCommand(bensonIdentity, MATH_SESSION.getId());
        String expected = AttendanceMarkSessionCommand.class.getCanonicalName()
                + "{identity=" + bensonIdentity + ", sessionId=" + MATH_SESSION.getId() + "}";
        assertEquals(expected, attendanceMarkSessionCommand.toString());
    }

}
