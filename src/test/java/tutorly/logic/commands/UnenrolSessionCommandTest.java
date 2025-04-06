package tutorly.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static tutorly.testutil.Assert.assertThrows;
import static tutorly.testutil.TypicalAddressBook.ALICE;
import static tutorly.testutil.TypicalAddressBook.BENSON;
import static tutorly.testutil.TypicalAddressBook.BENSON_ATTEND_MATH;
import static tutorly.testutil.TypicalAddressBook.ENGLISH_SESSION;
import static tutorly.testutil.TypicalAddressBook.MATH_SESSION;
import static tutorly.testutil.TypicalAddressBook.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import tutorly.logic.Messages;
import tutorly.logic.commands.exceptions.CommandException;
import tutorly.model.Model;
import tutorly.model.ModelManager;
import tutorly.model.UserPrefs;
import tutorly.model.person.Identity;

public class UnenrolSessionCommandTest {
    private static final int INVALID_ID = 999;
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final Identity bensonIdentity = new Identity(BENSON.getId());

    @Test
    public void constructor_nullArg_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new UnenrolSessionCommand(null, 1));
    }

    @Test
    public void execute_attendanceRecordRemovedByModel_removeSuccessful() throws Exception {
        CommandResult commandResult = new UnenrolSessionCommand(bensonIdentity, MATH_SESSION.getId()).execute(model);
        assertEquals(String.format(UnenrolSessionCommand.MESSAGE_SUCCESS, BENSON.getName().fullName,
                Messages.format(MATH_SESSION)), commandResult.getFeedbackToUser());

        assertFalse(model.hasAttendanceRecord(BENSON_ATTEND_MATH));
    }

    @Test
    public void execute_invalidIdentity_throwsCommandException() {
        UnenrolSessionCommand unenrolSessionCommand =
                new UnenrolSessionCommand(new Identity(INVALID_ID), ENGLISH_SESSION.getId());

        assertThrows(
                CommandException.class, Messages.MESSAGE_PERSON_NOT_FOUND, () -> unenrolSessionCommand.execute(model));
    }

    @Test
    public void execute_invalidSessionId_throwsCommandException() {
        UnenrolSessionCommand unenrolSessionCommand = new UnenrolSessionCommand(bensonIdentity, INVALID_ID);

        assertThrows(CommandException.class,
                Messages.MESSAGE_SESSION_NOT_FOUND, () -> unenrolSessionCommand.execute(model));
    }

    @Test
    public void execute_missingAttendanceRecord_throwsCommandException() {
        UnenrolSessionCommand unenrolSessionCommand =
                new UnenrolSessionCommand(bensonIdentity, ENGLISH_SESSION.getId());

        assertThrows(CommandException.class, String.format(
                UnenrolSessionCommand.MESSAGE_MISSING_ENROLMENT,
                BENSON.getName().fullName,
                Messages.format(ENGLISH_SESSION)), () -> unenrolSessionCommand.execute(model));
    }

    @Test
    public void equals() {
        UnenrolSessionCommand unenrolBensonMathCommand =
                new UnenrolSessionCommand(bensonIdentity, MATH_SESSION.getId());
        UnenrolSessionCommand unenrolAliceMathCommand =
                new UnenrolSessionCommand(new Identity(ALICE.getId()), MATH_SESSION.getId());

        // same object -> returns true
        assertEquals(unenrolBensonMathCommand, unenrolBensonMathCommand);

        // same values -> returns true
        UnenrolSessionCommand unenrolBensonMathCommandCopy =
                new UnenrolSessionCommand(bensonIdentity, MATH_SESSION.getId());
        assertEquals(unenrolBensonMathCommand, unenrolBensonMathCommandCopy);

        // different types -> returns false
        assertNotEquals(1, unenrolBensonMathCommand);

        // null -> returns false
        assertNotEquals(null, unenrolBensonMathCommand);

        // different person -> returns false
        assertNotEquals(unenrolBensonMathCommand, unenrolAliceMathCommand);

        // different session -> returns false
        assertNotEquals(unenrolBensonMathCommand, new UnenrolSessionCommand(bensonIdentity, ENGLISH_SESSION.getId()));
    }

    @Test
    public void toStringMethod() {
        UnenrolSessionCommand unenrolSessionCommand =
                new UnenrolSessionCommand(bensonIdentity, MATH_SESSION.getId());
        String expected = UnenrolSessionCommand.class.getCanonicalName()
                + "{identity=" + bensonIdentity + ", sessionId=" + MATH_SESSION.getId() + "}";
        assertEquals(expected, unenrolSessionCommand.toString());
    }
}
