package tutorly.logic;

import java.nio.file.Path;

import javafx.collections.ObservableList;
import tutorly.commons.core.GuiSettings;
import tutorly.logic.commands.Command;
import tutorly.logic.commands.CommandResult;
import tutorly.logic.commands.exceptions.CommandException;
import tutorly.logic.parser.exceptions.ParseException;
import tutorly.model.Model;
import tutorly.model.ReadOnlyAddressBook;
import tutorly.model.attendancerecord.AttendanceRecord;
import tutorly.model.person.Person;
import tutorly.model.session.Session;

/**
 * API of the Logic component
 */
public interface Logic {
    /**
     * Executes the command and returns the result.
     * @param commandText The command as entered by the user.
     * @return the result of the command execution.
     * @throws CommandException If an error occurs during command execution.
     * @throws ParseException If an error occurs during parsing.
     */
    CommandResult execute(String commandText) throws CommandException, ParseException;

    /**
     * Executes the command and returns the result.
     * @param command The command to be executed.
     * @return the result of the command execution.
     * @throws CommandException If an error occurs during command execution.
     */
    CommandResult execute(Command command) throws CommandException;

    /**
     * Returns the AddressBook.
     *
     * @see Model#getAddressBook()
     */
    ReadOnlyAddressBook getAddressBook();

    /** Returns an unmodifiable view of the list of persons */
    ObservableList<Person> getPersonList();

    /** Returns an unmodifiable view of the filtered list of persons */
    ObservableList<Person> getFilteredPersonList();

    /** Returns an unmodifiable view of the list of sessions */
    ObservableList<Session> getSessionList();

    /** Returns an unmodifiable view of the filtered list of sessions */
    ObservableList<Session> getFilteredSessionList();

    /** Returns an unmodifiable view of the list of attendance records */
    ObservableList<AttendanceRecord> getAttendanceRecordList();

    /**
     * Returns the user prefs' address book file path.
     */
    Path getAddressBookFilePath();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Set the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);
}
