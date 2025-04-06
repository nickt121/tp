package tutorly.logic.commands;

import static java.util.Objects.requireNonNull;
import static tutorly.logic.parser.CliSyntax.PREFIX_SESSION;

import java.util.Optional;

import tutorly.commons.util.ToStringBuilder;
import tutorly.logic.Messages;
import tutorly.logic.commands.exceptions.CommandException;
import tutorly.model.Model;
import tutorly.model.attendancerecord.AttendanceRecord;
import tutorly.model.attendancerecord.Feedback;
import tutorly.model.person.Identity;
import tutorly.model.person.Person;
import tutorly.model.session.Session;
import tutorly.ui.Tab;

/**
 * Unmarks attendance for a student in a session.
 */
public class AttendanceUnmarkSessionCommand extends SessionCommand {

    public static final String COMMAND_WORD = "unmark";
    public static final String COMMAND_STRING = SessionCommand.COMMAND_STRING + " " + COMMAND_WORD;

    public static final String MESSAGE_USAGE = COMMAND_STRING
            + ": Unmarks attendance for a student in a session."
            + "\nParameters: STUDENT_IDENTIFIER "
            + PREFIX_SESSION + "SESSION_ID"
            + "\nExample: " + COMMAND_STRING + " 1 "
            + PREFIX_SESSION + "2";

    public static final String MESSAGE_SUCCESS = "Unmarked %1$s's for Session: %2$s";
    public static final String MESSAGE_RECORD_NOT_FOUND = "%1$s is not assigned to Session: %2$s";
    public static final String MESSAGE_ALREADY_UNMARKED = "%1$s's attendance is not marked for Session: %2$s";

    private final Identity identity;
    private final int sessionId;

    /**
     * Creates an AttendanceUnmarkSessionCommand for the given {@code identity} and {@code Session}
     */
    public AttendanceUnmarkSessionCommand(Identity identity, int sessionId) {
        this.identity = requireNonNull(identity);
        this.sessionId = sessionId;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Optional<Person> person = model.getPersonByIdentity(identity);
        if (person.isEmpty()) {
            throw new CommandException(Messages.MESSAGE_PERSON_NOT_FOUND);
        }

        Optional<Session> session = model.getSessionById(sessionId);
        if (session.isEmpty()) {
            throw new CommandException(Messages.MESSAGE_INVALID_SESSION_ID);
        }

        AttendanceRecord dummyRecord = new AttendanceRecord(person.get().getId(), sessionId,
                false, Feedback.empty());
        Optional<AttendanceRecord> existingRecord = model.findAttendanceRecord(dummyRecord);
        if (existingRecord.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_RECORD_NOT_FOUND,
                    person.get().getName().fullName, Messages.format(session.get())));
        }

        if (!existingRecord.get().getAttendance()) {
            throw new CommandException(String.format(MESSAGE_ALREADY_UNMARKED,
                    person.get().getName().fullName, Messages.format(session.get())));
        }

        AttendanceRecord record = new AttendanceRecord(person.get().getId(), sessionId,
                false, existingRecord.get().getFeedback());

        model.setAttendanceRecord(existingRecord.get(), record);
        model.updateFilteredSessionList(Model.FILTER_SHOW_ALL_SESSIONS);
        return new CommandResult.Builder(
                String.format(MESSAGE_SUCCESS, person.get().getName().fullName, Messages.format(session.get())))
                .withTab(Tab.attendanceRecord(session.get(), record))
                .withReverseCommand(new AttendanceMarkSessionCommand(identity, sessionId))
                .build();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AttendanceUnmarkSessionCommand otherAttendanceUnmarkSessionCommand)) {
            return false;
        }

        return identity.equals(otherAttendanceUnmarkSessionCommand.identity)
                && sessionId == otherAttendanceUnmarkSessionCommand.sessionId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("identity", identity)
                .add("sessionId", sessionId)
                .toString();
    }

}
