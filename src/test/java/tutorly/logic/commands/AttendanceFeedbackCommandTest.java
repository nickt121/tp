package tutorly.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static tutorly.logic.Messages.MESSAGE_PERSON_NOT_FOUND;
import static tutorly.logic.commands.AttendanceFeedbackCommand.MESSAGE_SUCCESS;
import static tutorly.logic.commands.CommandTestUtil.assertCommandFailure;
import static tutorly.logic.commands.CommandTestUtil.assertCommandSuccess;
import static tutorly.logic.parser.ParserUtil.parseFeedback;
import static tutorly.testutil.Assert.assertThrows;
import static tutorly.testutil.TypicalAddressBook.BENSON;
import static tutorly.testutil.TypicalAddressBook.DANIEL;
import static tutorly.testutil.TypicalAddressBook.ENGLISH_SESSION;
import static tutorly.testutil.TypicalAddressBook.IDA;
import static tutorly.testutil.TypicalAddressBook.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import tutorly.logic.Messages;
import tutorly.logic.commands.exceptions.CommandException;
import tutorly.logic.parser.exceptions.ParseException;
import tutorly.model.Model;
import tutorly.model.ModelManager;
import tutorly.model.UserPrefs;
import tutorly.model.attendancerecord.AttendanceRecord;
import tutorly.model.attendancerecord.Feedback;
import tutorly.model.person.Identity;
import tutorly.model.person.Person;
import tutorly.model.session.Session;

public class AttendanceFeedbackCommandTest {
    private static final int INVALID_ID = 999;
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final Identity bensonIdentity = new Identity(BENSON.getId());
    private final Identity danielIdentity = new Identity(DANIEL.getId());
    private final Identity idaIdentity = new Identity(IDA.getId());

    @Test
    public void constructor_nullArg_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new AttendanceFeedbackCommand(bensonIdentity, 1, null));
    }

    @Test
    public void execute_validRecord_feedbackSuccessful() throws ParseException {
        Session sessionToEdit = model.getSessionById(1).get();
        Person person = model.getPersonByIdentity(bensonIdentity).get();
        AttendanceFeedbackCommand command = new AttendanceFeedbackCommand(
                bensonIdentity, 1, parseFeedback("Good job!"));
        String expectedMessage = String.format(MESSAGE_SUCCESS, person.getName().fullName,
                Messages.format(sessionToEdit));
        AttendanceRecord expectedRecord = new AttendanceRecord(
                person.getId(), sessionToEdit.getId(), true, new Feedback("Good job!"));

        // same model passed in for both command and expected model as model equals check does not care about feedback
        assertCommandSuccess(command, model, expectedMessage, model);
        assertEquals(new Feedback("Good job!"), model.findAttendanceRecord(expectedRecord).get().getFeedback());
    }

    @Test
    public void execute_personNotFound_throwsCommandException() throws ParseException {
        Session sessionToEdit = model.getSessionById(1).get();
        Person person = model.getPersonByIdentity(danielIdentity).get();
        AttendanceFeedbackCommand command = new AttendanceFeedbackCommand(
                idaIdentity, 1, parseFeedback("Good job!"));

        assertThrows(CommandException.class, () -> command.execute(model));
        assertCommandFailure(command, model, MESSAGE_PERSON_NOT_FOUND);
    }

    @Test
    public void execute_invalidSessionId_throwsCommandException() throws ParseException {
        AttendanceFeedbackCommand command = new AttendanceFeedbackCommand(
                bensonIdentity, INVALID_ID, parseFeedback("Good job!"));

        assertThrows(CommandException.class, () -> command.execute(model));
        assertCommandFailure(command, model, Messages.MESSAGE_SESSION_NOT_FOUND);
    }

    @Test
    public void execute_recordNotFound_throwsCommandException() throws ParseException {
        Person person = model.getPersonByIdentity(bensonIdentity).get();
        AttendanceFeedbackCommand command = new AttendanceFeedbackCommand(
                bensonIdentity, ENGLISH_SESSION.getId(), parseFeedback("Good job!"));

        assertThrows(CommandException.class, () -> command.execute(model));
        assertCommandFailure(command, model,
                String.format(AttendanceFeedbackCommand.MESSAGE_RECORD_NOT_FOUND,
                        person.getName().fullName, Messages.format(ENGLISH_SESSION)));
    }

    @Test
    public void equals() throws ParseException {
        Feedback feedback1 = parseFeedback("Feedback 1");
        Feedback feedback2 = parseFeedback("Feedback 2");

        AttendanceFeedbackCommand command1 = new AttendanceFeedbackCommand(
                bensonIdentity, 1, feedback1);
        AttendanceFeedbackCommand command2 = new AttendanceFeedbackCommand(
                bensonIdentity, 1, feedback2);
        AttendanceFeedbackCommand command3 = new AttendanceFeedbackCommand(
                bensonIdentity, 2, feedback2);

        // same object -> returns true
        assertEquals(command1, command1);

        // same values -> returns true
        AttendanceFeedbackCommand command1Copy = new AttendanceFeedbackCommand(
                bensonIdentity, 1, feedback1);
        assertEquals(command1, command1Copy);

        // different types -> returns false
        assertNotEquals(new Object(), command1);

        // null -> returns false
        assertNotEquals(null, command1);

        // different session -> returns false
        assertNotEquals(command1, command2);
        assertNotEquals(command2, command3);
    }

    @Test
    public void toStringMethod() throws ParseException {
        Feedback feedback = parseFeedback("Good job!");
        AttendanceFeedbackCommand command = new AttendanceFeedbackCommand(
                bensonIdentity, 1, feedback);
        String expected = AttendanceFeedbackCommand.class.getCanonicalName()
                + "{identity=" + bensonIdentity
                + ", sessionId=1"
                + ", feedback=" + feedback
                + "}";
        assertEquals(expected, command.toString());
    }
}
