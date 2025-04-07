package tutorly.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tutorly.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tutorly.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static tutorly.logic.parser.CliSyntax.PREFIX_DATE;
import static tutorly.logic.parser.CliSyntax.PREFIX_FEEDBACK;
import static tutorly.logic.parser.CliSyntax.PREFIX_NAME;
import static tutorly.logic.parser.CliSyntax.PREFIX_PHONE;
import static tutorly.logic.parser.CliSyntax.PREFIX_SESSION;
import static tutorly.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static tutorly.logic.parser.ParserUtil.DATE_FORMATTER;
import static tutorly.testutil.Assert.assertThrows;
import static tutorly.testutil.TypicalIdentities.IDENTITY_FIRST_PERSON;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import tutorly.logic.commands.AddSessionCommand;
import tutorly.logic.commands.AddStudentCommand;
import tutorly.logic.commands.AttendanceFeedbackCommand;
import tutorly.logic.commands.AttendanceMarkSessionCommand;
import tutorly.logic.commands.AttendanceUnmarkSessionCommand;
import tutorly.logic.commands.ClearCommand;
import tutorly.logic.commands.DeleteSessionCommand;
import tutorly.logic.commands.DeleteStudentCommand;
import tutorly.logic.commands.EditSessionCommand;
import tutorly.logic.commands.EditSessionCommand.EditSessionDescriptor;
import tutorly.logic.commands.EditStudentCommand;
import tutorly.logic.commands.EditStudentCommand.EditPersonDescriptor;
import tutorly.logic.commands.EnrolSessionCommand;
import tutorly.logic.commands.ExitCommand;
import tutorly.logic.commands.HelpCommand;
import tutorly.logic.commands.ListSessionCommand;
import tutorly.logic.commands.ListStudentCommand;
import tutorly.logic.commands.SearchSessionCommand;
import tutorly.logic.commands.SearchStudentCommand;
import tutorly.logic.commands.SessionCommand;
import tutorly.logic.commands.StudentCommand;
import tutorly.logic.commands.UndoCommand;
import tutorly.logic.commands.UnenrolSessionCommand;
import tutorly.logic.parser.exceptions.ParseException;
import tutorly.model.attendancerecord.Feedback;
import tutorly.model.filter.AttendSessionFilter;
import tutorly.model.filter.DateSessionFilter;
import tutorly.model.filter.Filter;
import tutorly.model.filter.NameContainsKeywordsFilter;
import tutorly.model.filter.PhoneContainsKeywordsFilter;
import tutorly.model.filter.SubjectContainsKeywordsFilter;
import tutorly.model.person.Identity;
import tutorly.model.person.Person;
import tutorly.model.session.Session;
import tutorly.testutil.EditPersonDescriptorBuilder;
import tutorly.testutil.EditSessionDescriptorBuilder;
import tutorly.testutil.PersonBuilder;
import tutorly.testutil.PersonUtil;
import tutorly.testutil.SessionBuilder;
import tutorly.testutil.SessionUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parse(HelpCommand.COMMAND_STRING) instanceof HelpCommand);
        assertTrue(parser.parse(HelpCommand.COMMAND_STRING + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parse(ClearCommand.COMMAND_STRING) instanceof ClearCommand);
        assertTrue(parser.parse(ClearCommand.COMMAND_STRING + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parse(ExitCommand.COMMAND_STRING) instanceof ExitCommand);
        assertTrue(parser.parse(ExitCommand.COMMAND_STRING + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_undo() throws Exception {
        assertTrue(parser.parse(UndoCommand.COMMAND_STRING) instanceof UndoCommand);
        assertTrue(parser.parse(UndoCommand.COMMAND_STRING + " 3") instanceof UndoCommand);
    }

    @Test
    public void parseCommand_student() throws Exception {
        assertTrue(parser.parse(StudentCommand.COMMAND_WORD) instanceof StudentCommand);
    }

    @Test
    public void parseCommand_session() throws Exception {
        assertTrue(parser.parse(SessionCommand.COMMAND_WORD) instanceof SessionCommand);
    }

    @Test
    public void parseCommand_studentAdd() throws Exception {
        Person person = new PersonBuilder().build();
        AddStudentCommand command = (AddStudentCommand) parser.parse(PersonUtil.getAddCommand(person));
        assertEquals(new AddStudentCommand(person), command);
    }

    @Test
    public void parseCommand_studentList() throws Exception {
        assertTrue(parser.parse(ListStudentCommand.COMMAND_STRING) instanceof ListStudentCommand);
        assertTrue(parser.parse(ListStudentCommand.COMMAND_STRING + " 3") instanceof ListStudentCommand);
    }

    @Test
    public void parseCommand_studentEdit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditStudentCommand command = (EditStudentCommand) parser.parse(EditStudentCommand.COMMAND_STRING + " "
                + IDENTITY_FIRST_PERSON.getId() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditStudentCommand(IDENTITY_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_studentSearch() throws Exception {
        int sessionId = 1;
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        SearchStudentCommand command = (SearchStudentCommand) parser.parse(
                SearchStudentCommand.COMMAND_STRING
                        + " " + PREFIX_SESSION + sessionId
                        + " " + PREFIX_NAME + keywords.stream().collect(Collectors.joining(" "))
                        + " " + PREFIX_PHONE + keywords.stream().collect(Collectors.joining(" ")));
        Filter<Person> filter = Filter.any(Arrays.asList(
                new AttendSessionFilter(sessionId),
                new NameContainsKeywordsFilter(keywords),
                new PhoneContainsKeywordsFilter(keywords)));
        assertEquals(new SearchStudentCommand(filter), command);
    }

    @Test
    public void parseCommand_studentDelete() throws Exception {
        DeleteStudentCommand command = (DeleteStudentCommand) parser.parse(
                DeleteStudentCommand.COMMAND_STRING + " " + IDENTITY_FIRST_PERSON.getId());
        assertEquals(new DeleteStudentCommand(IDENTITY_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_sessionAdd() throws Exception {
        Session session = new SessionBuilder().build();
        AddSessionCommand command = (AddSessionCommand) parser.parse(SessionUtil.getAddCommand(session));
        assertEquals(new AddSessionCommand(session), command);
    }

    @Test
    public void parseCommand_sessionList() throws Exception {
        assertTrue(parser.parse(ListSessionCommand.COMMAND_STRING) instanceof ListSessionCommand);
        assertTrue(parser.parse(ListSessionCommand.COMMAND_STRING + " 3") instanceof ListSessionCommand);
    }

    @Test
    public void parseCommand_sessionEdit() throws Exception {
        Session session = new SessionBuilder().build();
        EditSessionDescriptor descriptor = new EditSessionDescriptorBuilder(session).build();
        EditSessionCommand command = (EditSessionCommand) parser.parse(EditSessionCommand.COMMAND_STRING + " 3 "
                + SessionUtil.getEditSessionDescriptorDetails(descriptor));
        assertEquals(new EditSessionCommand(3, descriptor), command);
    }

    @Test
    public void parseCommand_sessionSearch() throws Exception {
        LocalDate date = LocalDate.of(2025, 1, 1);
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        SearchSessionCommand command = (SearchSessionCommand) parser.parse(
                SearchSessionCommand.COMMAND_STRING
                        + " " + PREFIX_DATE + date.format(DATE_FORMATTER)
                        + " " + PREFIX_SUBJECT + keywords.stream().collect(Collectors.joining(" ")));
        Filter<Session> filter = Filter.any(Arrays.asList(
                new DateSessionFilter(date),
                new SubjectContainsKeywordsFilter(keywords)));
        assertEquals(new SearchSessionCommand(filter), command);
    }

    @Test
    public void parseCommand_sessionDelete() throws Exception {
        DeleteSessionCommand command = (DeleteSessionCommand) parser.parse(
                DeleteSessionCommand.COMMAND_STRING + " 3");
        assertEquals(new DeleteSessionCommand(3), command);
    }

    @Test
    public void parseCommand_sessionEnrol() throws Exception {
        int id = 1;
        Identity identity = new Identity(id);
        EnrolSessionCommand command = (EnrolSessionCommand) parser.parse(
                EnrolSessionCommand.COMMAND_STRING + " " + id + " " + PREFIX_SESSION + id);
        assertEquals(new EnrolSessionCommand(identity, id), command);
    }

    @Test
    public void parseCommand_sessionUnenrol() throws Exception {
        int id = 1;
        Identity identity = new Identity(id);
        UnenrolSessionCommand command = (UnenrolSessionCommand) parser.parse(
                UnenrolSessionCommand.COMMAND_STRING + " " + id + " " + PREFIX_SESSION + id);
        assertEquals(new UnenrolSessionCommand(identity, id), command);
    }

    @Test
    public void parseCommand_sessionMark() throws Exception {
        int id = 1;
        Identity identity = new Identity(id);
        AttendanceMarkSessionCommand command = (AttendanceMarkSessionCommand) parser.parse(
                AttendanceMarkSessionCommand.COMMAND_STRING + " " + id + " " + PREFIX_SESSION + id);
        assertEquals(new AttendanceMarkSessionCommand(identity, id), command);
    }

    @Test
    public void parseCommand_sessionUnmark() throws Exception {
        int id = 1;
        Identity identity = new Identity(id);
        AttendanceUnmarkSessionCommand command = (AttendanceUnmarkSessionCommand) parser.parse(
                AttendanceUnmarkSessionCommand.COMMAND_STRING + " " + id + " " + PREFIX_SESSION + id);
        assertEquals(new AttendanceUnmarkSessionCommand(identity, id), command);
    }

    @Test
    public void parseCommand_sessionFeedback() throws Exception {
        int id = 1;
        Identity identity = new Identity(id);
        String feedback = "Good job!";
        AttendanceFeedbackCommand command = (AttendanceFeedbackCommand) parser.parse(
                AttendanceFeedbackCommand.COMMAND_STRING + " " + id + " " + PREFIX_SESSION + id
                        + " " + PREFIX_FEEDBACK + feedback);
        assertEquals(new AttendanceFeedbackCommand(identity, id, new Feedback(feedback)), command);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
                -> parser.parse(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parse("unknownCommand"));
    }

}
