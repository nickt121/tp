package tutorly.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import tutorly.commons.core.GuiSettings;
import tutorly.logic.Messages;
import tutorly.logic.commands.exceptions.CommandException;
import tutorly.model.Model;
import tutorly.model.ReadOnlyAddressBook;
import tutorly.model.ReadOnlyUserPrefs;
import tutorly.model.attendancerecord.AttendanceRecord;
import tutorly.model.filter.Filter;
import tutorly.model.person.Name;
import tutorly.model.person.Person;
import tutorly.model.session.Session;
import tutorly.model.session.UniqueSessionList;

/**
 * Test class for AddSessionCommand.
 */
public class AddSessionCommandTest {

    private ModelStub model;
    private Session session;
    private AddSessionCommand addSessionCommand;

    @BeforeEach
    void setUp() {
        model = new ModelStub();
        session = new Session(101, LocalDate.of(2025, 3, 18), "Mathematics");
        addSessionCommand = new AddSessionCommand(session);
    }

    @Test
    void execute_success() throws CommandException {
        CommandResult result = addSessionCommand.execute(model);
        assertEquals(String.format(AddSessionCommand.MESSAGE_SUCCESS, Messages.format(session)),
                result.getFeedbackToUser());
    }

    @Test
    void execute_duplicateSession_throwsException() throws CommandException {
        addSessionCommand.execute(model); // Add session first
        assertThrows(CommandException.class, () -> addSessionCommand.execute(model));
    }

    @Test
    void equals_sameObject() {
        assertEquals(addSessionCommand, addSessionCommand);
    }

    @Test
    void equals_differentObjectSameData() {
        AddSessionCommand commandCopy = new AddSessionCommand(session);
        assertEquals(addSessionCommand, commandCopy);
    }

    @Test
    void equals_differentObjects() {
        Session differentSession = new Session(102, LocalDate.of(2025, 3, 19), "Science");
        AddSessionCommand differentCommand = new AddSessionCommand(differentSession);
        assertNotEquals(addSessionCommand, differentCommand);
    }

    @Test
    void toStringTest() {
        String expected = "AddSessionCommand{toCreate=" + session + "}";
        assertTrue(addSessionCommand.toString().contains(expected));
    }

    /**
     * A simple stub class for Model that uses an in-memory UniqueSessionList.
     */
    private static class ModelStub implements Model {
        private final UniqueSessionList sessions = new UniqueSessionList();

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            return null;
        }

        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        }

        @Override
        public GuiSettings getGuiSettings() {
            return null;
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
        }

        @Override
        public Path getAddressBookFilePath() {
            return null;
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return null;
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook addressBook) {
        }

        @Override
        public boolean hasPerson(Person person) {
            return false;
        }

        @Override
        public void deletePerson(Person target) {
        }

        @Override
        public void restorePerson(Person target) {
        }

        @Override
        public void addPerson(Person person) {
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
        }

        @Override
        public Optional<Person> getPersonById(int id, boolean fromArchived) {
            return Optional.empty();
        }

        @Override
        public Optional<Person> getPersonByName(Name name, boolean fromArchived) {
            return Optional.empty();
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return null;
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            return null;
        }

        @Override
        public ObservableList<Session> getSessionList() {
            return null;
        }

        @Override
        public ObservableList<Session> getFilteredSessionList() {
            return null;
        }

        @Override
        public ObservableList<AttendanceRecord> getAttendanceRecordList() {
            return null;
        }

        @Override
        public ObservableList<Person> getArchivedPersonList() {
            return null;
        }

        @Override
        public void updateFilteredPersonList(Filter<Person> predicate) {
        }

        @Override
        public void updateFilteredSessionList(Filter<Session> predicate) {
        }

        @Override
        public boolean hasSession(Session session) {
            return sessions.contains(session);
        }

        @Override
        public void addSession(Session session) {
            sessions.add(session);
        }

        @Override
        public Optional<Session> getSessionById(int id) {
            return Optional.empty();
        }

        @Override
        public boolean hasAttendanceRecord(AttendanceRecord record) {
            return false;
        }

        @Override
        public void addAttendanceRecord(AttendanceRecord record) {
        }

        @Override
        public void removeAttendanceRecord(AttendanceRecord record) {
        }
    }
}
