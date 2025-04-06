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
 * Creates a new AttendanceRecord for a student to a session.
 */
public class EnrolSessionCommand extends SessionCommand {

    public static final String COMMAND_WORD = "enrol";
    public static final String COMMAND_STRING = SessionCommand.COMMAND_STRING + " " + COMMAND_WORD;

    public static final String MESSAGE_USAGE = COMMAND_STRING
            + ": Enrols a student identified by a STUDENT_IDENTIFIER (ID or full name) to a session."
            + "\nParameters: STUDENT_IDENTIFIER "
            + PREFIX_SESSION + "SESSION_ID"
            + "\nExample: " + COMMAND_STRING + " 1 "
            + PREFIX_SESSION + "2";

    public static final String MESSAGE_SUCCESS = "%1$s enrolled to Session: %2$s";
    public static final String MESSAGE_DUPLICATE_ENROLMENT = "This student is already enrolled in the session";
    public static final boolean DEFAULT_PRESENCE = false;

    private final Identity identity;
    private final int sessionId;
    private final boolean presence;
    private final Feedback feedback;

    /**
     * Creates an EnrolSessionCommand for the given {@code Person} to the given {@code Session} with the given
     * {@code presence} status and {@code feedback}.
     */
    public EnrolSessionCommand(Identity identity, int sessionId, boolean presence, Feedback feedback) {
        requireNonNull(identity);
        requireNonNull(feedback);
        this.identity = identity;
        this.sessionId = sessionId;
        this.presence = presence;
        this.feedback = feedback;
    }

    /**
     * Creates an EnrolSessionCommand for the given {@code Person} to the given {@code Session}.
     */
    public EnrolSessionCommand(Identity identity, int sessionId) {
        this(identity, sessionId, DEFAULT_PRESENCE, Feedback.empty());
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

        AttendanceRecord record = new AttendanceRecord(person.get().getId(), sessionId, presence, feedback);
        if (model.hasAttendanceRecord(record)) {
            throw new CommandException(MESSAGE_DUPLICATE_ENROLMENT);
        }

        model.addAttendanceRecord(record);
        return new CommandResult.Builder(
                String.format(MESSAGE_SUCCESS, person.get().getName().fullName, Messages.format(session.get())))
                .withTab(Tab.attendanceRecord(session.get(), record))
                .withReverseCommand(new UnenrolSessionCommand(identity, sessionId))
                .build();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EnrolSessionCommand otherEnrolSessionCommand)) {
            return false;
        }

        return identity.equals(otherEnrolSessionCommand.identity)
                && sessionId == otherEnrolSessionCommand.sessionId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("identity", identity)
                .add("sessionId", sessionId)
                .toString();
    }
}
