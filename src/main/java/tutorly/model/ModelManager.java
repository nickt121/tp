package tutorly.model;

import static java.util.Objects.requireNonNull;
import static tutorly.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import tutorly.commons.core.GuiSettings;
import tutorly.commons.core.LogsCenter;
import tutorly.model.attendancerecord.AttendanceRecord;
import tutorly.model.filter.Filter;
import tutorly.model.person.Identity;
import tutorly.model.person.Name;
import tutorly.model.person.Person;
import tutorly.model.session.Session;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final FilteredList<Session> filteredSessions;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        filteredSessions = new FilteredList<>(this.addressBook.getSessionList());
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    //=========== AddressBook ================================================================================

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
    }

    @Override
    public Optional<Person> getPersonById(int id) {
        return addressBook.getPersonById(id);
    }

    @Override
    public Optional<Person> getPersonByName(Name name) {
        return addressBook.getPersonByName(name);
    }

    @Override
    public Optional<Person> getPersonByIdentity(Identity identity) {
        if (identity.isIdPresent()) {
            return getPersonById(identity.getId());
        } else if (identity.isNamePresent()) {
            return getPersonByName(identity.getName());
        }

        return Optional.empty();
    }

    //=========== Filtered Person List Accessors =============================================================

    @Override
    public ObservableList<Person> getPersonList() {
        return addressBook.getPersonList();
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public ObservableList<Session> getSessionList() {
        return addressBook.getSessionList();
    }

    @Override
    public ObservableList<Session> getFilteredSessionList() {
        return filteredSessions;
    }

    @Override
    public ObservableList<AttendanceRecord> getAttendanceRecordList() {
        return addressBook.getAttendanceRecordsList();
    }

    @Override
    public void updateFilteredPersonList(Filter<Person> filter) {
        requireNonNull(filter);
        filteredPersons.setPredicate(filter.toPredicate(getAddressBook()));
    }

    @Override
    public void updateFilteredSessionList(Filter<Session> filter) {
        requireNonNull(filter);
        filteredSessions.setPredicate(filter.toPredicate(getAddressBook()));
    }

    @Override
    public boolean hasSession(Session toCreate) {
        requireAllNonNull(toCreate);
        return addressBook.hasSession(toCreate);
    }

    @Override
    public boolean hasOverlappingSession(Session toCreate) {
        requireAllNonNull(toCreate);
        return addressBook.hasOverlappingSession(toCreate);
    }

    @Override
    public void addSession(Session toCreate) {
        requireAllNonNull(toCreate);
        addressBook.addSession(toCreate);
    }

    @Override
    public void deleteSession(Session target) {
        addressBook.removeSession(target);
    }

    @Override
    public void setSession(Session target, Session editedSession) {
        requireAllNonNull(target, editedSession);
        addressBook.setSession(target, editedSession);
    }

    @Override
    public Optional<Session> getSessionById(int id) {
        return addressBook.getSessionById(id);
    }

    @Override
    public boolean hasAttendanceRecord(AttendanceRecord record) {
        requireNonNull(record);
        return addressBook.hasAttendanceRecord(record);
    }

    @Override
    public Optional<AttendanceRecord> findAttendanceRecord(AttendanceRecord record) {
        requireNonNull(record);
        return addressBook.findAttendanceRecord(record);
    }

    @Override
    public void addAttendanceRecord(AttendanceRecord record) {
        requireNonNull(record);
        addressBook.addAttendanceRecord(record);
    }

    @Override
    public void removeAttendanceRecord(AttendanceRecord record) {
        requireNonNull(record);
        addressBook.removeAttendanceRecord(record);
    }

    @Override
    public void setAttendanceRecord(AttendanceRecord target, AttendanceRecord editedRecord) {
        requireAllNonNull(target, editedRecord);

        addressBook.setAttendanceRecord(target, editedRecord);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons)
                && filteredSessions.equals(otherModelManager.filteredSessions);
    }

}
