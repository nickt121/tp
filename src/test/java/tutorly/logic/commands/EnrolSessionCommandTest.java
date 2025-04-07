package tutorly.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tutorly.testutil.Assert.assertThrows;
import static tutorly.testutil.TypicalAddressBook.ALICE;
import static tutorly.testutil.TypicalAddressBook.BENSON;
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

public class EnrolSessionCommandTest {
    private static final int INVALID_ID = 999;
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final Identity bensonIdentity = new Identity(BENSON.getId());

    @Test
    public void constructor_nullArg_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EnrolSessionCommand(null, 1));
    }

    @Test
    public void execute_attendanceRecordAcceptedByModel_addSuccessful() throws Exception {
        CommandResult commandResult = new EnrolSessionCommand(bensonIdentity, ENGLISH_SESSION.getId()).execute(model);
        assertEquals(String.format(EnrolSessionCommand.MESSAGE_SUCCESS, BENSON.getName().fullName,
                Messages.format(ENGLISH_SESSION)), commandResult.getFeedbackToUser());

        AttendanceRecord expectedRecord = new AttendanceRecord(
                BENSON.getId(), ENGLISH_SESSION.getId(), EnrolSessionCommand.DEFAULT_PRESENCE, Feedback.empty());
        assertTrue(model.hasAttendanceRecord(expectedRecord));
    }

    @Test
    public void execute_invalidIdentity_throwsCommandException() {
        EnrolSessionCommand enrolSessionCommand =
                new EnrolSessionCommand(new Identity(INVALID_ID), ENGLISH_SESSION.getId());

        assertThrows(
                CommandException.class, Messages.MESSAGE_PERSON_NOT_FOUND, () -> enrolSessionCommand.execute(model));
    }

    @Test
    public void execute_invalidSessionId_throwsCommandException() {
        EnrolSessionCommand enrolSessionCommand = new EnrolSessionCommand(bensonIdentity, INVALID_ID);

        assertThrows(CommandException.class,
                Messages.MESSAGE_SESSION_NOT_FOUND, () -> enrolSessionCommand.execute(model));
    }

    @Test
    public void execute_duplicateAttendanceRecord_throwsCommandException() {
        EnrolSessionCommand enrolSessionCommand = new EnrolSessionCommand(bensonIdentity, MATH_SESSION.getId());

        assertThrows(CommandException.class,
                EnrolSessionCommand.MESSAGE_DUPLICATE_ENROLMENT, () -> enrolSessionCommand.execute(model));
    }

    @Test
    public void equals() {
        EnrolSessionCommand enrolBensonMathCommand = new EnrolSessionCommand(bensonIdentity, MATH_SESSION.getId());
        EnrolSessionCommand enrolAliceMathCommand =
                new EnrolSessionCommand(new Identity(ALICE.getId()), MATH_SESSION.getId());

        // same object -> returns true
        assertEquals(enrolBensonMathCommand, enrolBensonMathCommand);

        // same values -> returns true
        EnrolSessionCommand enrolBensonMathCommandCopy =
                new EnrolSessionCommand(bensonIdentity, MATH_SESSION.getId());
        assertEquals(enrolBensonMathCommand, enrolBensonMathCommandCopy);

        // different types -> returns false
        assertNotEquals(1, enrolBensonMathCommand);

        // null -> returns false
        assertNotEquals(null, enrolBensonMathCommand);

        // different person -> returns false
        assertNotEquals(enrolBensonMathCommand, enrolAliceMathCommand);

        // different session -> returns false
        assertNotEquals(enrolBensonMathCommand, new EnrolSessionCommand(bensonIdentity, ENGLISH_SESSION.getId()));
    }

    @Test
    public void toStringMethod() {
        EnrolSessionCommand enrolSessionCommand = new EnrolSessionCommand(bensonIdentity, MATH_SESSION.getId());
        String expected = EnrolSessionCommand.class.getCanonicalName()
                + "{identity=" + bensonIdentity + ", sessionId=" + MATH_SESSION.getId() + "}";
        assertEquals(expected, enrolSessionCommand.toString());
    }

}
