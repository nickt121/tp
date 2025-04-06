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
 * Marks attendance for a student in a session.
 */
public class AttendanceMarkSessionCommand extends SessionCommand {

    public static final String COMMAND_WORD = "mark";
    public static final String COMMAND_STRING = SessionCommand.COMMAND_STRING + " " + COMMAND_WORD;

    public static final String MESSAGE_USAGE = COMMAND_STRING
            + ": Marks attendance for a student in a session."
            + "\nParameters: STUDENT_IDENTIFIER "
            + PREFIX_SESSION + "SESSION_ID"
            + "\nExample: " + COMMAND_STRING + " 1 "
            + PREFIX_SESSION + "2";

    public static final String MESSAGE_SUCCESS = "Marked %1$s's for Session: %2$s";
    public static final String MESSAGE_RECORD_NOT_FOUND = "%1$s is not assigned to Session: %2$s";
    public static final String MESSAGE_ALREADY_MARKED = "%1$s's attendance is already marked for Session: %2$s";

    private final Identity identity;
    private final int sessionId;

    /**
     * Creates an AttendanceMarkSessionCommand for the given {@code identity} and {@code Session}
     */
    public AttendanceMarkSessionCommand(Identity identity, int sessionId) {
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
            throw new CommandException(Messages.MESSAGE_SESSION_NOT_FOUND);
        }

        AttendanceRecord dummyRecord = new AttendanceRecord(person.get().getId(), sessionId,
                true, Feedback.empty());
        Optional<AttendanceRecord> existingRecord = model.findAttendanceRecord(dummyRecord);
        if (existingRecord.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_RECORD_NOT_FOUND,
                    person.get().getName().fullName, Messages.format(session.get())));
        }

        if (existingRecord.get().getAttendance()) {
            throw new CommandException(String.format(MESSAGE_ALREADY_MARKED,
                    person.get().getName().fullName, Messages.format(session.get())));
        }

        AttendanceRecord record = new AttendanceRecord(person.get().getId(), sessionId,
                true, existingRecord.get().getFeedback());

        model.setAttendanceRecord(existingRecord.get(), record);
        return new CommandResult.Builder(
                String.format(MESSAGE_SUCCESS, person.get().getName().fullName, Messages.format(session.get())))
                .withTab(Tab.attendanceRecord(session.get(), record))
                .withReverseCommand(new AttendanceUnmarkSessionCommand(identity, sessionId))
                .build();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AttendanceMarkSessionCommand otherAttendanceMarkSessionCommand)) {
            return false;
        }

        return identity.equals(otherAttendanceMarkSessionCommand.identity)
                && sessionId == otherAttendanceMarkSessionCommand.sessionId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("identity", identity)
                .add("sessionId", sessionId)
                .toString();
    }

}
