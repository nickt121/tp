package tutorly.logic.commands;

import static java.util.Objects.requireNonNull;
import static tutorly.logic.parser.CliSyntax.PREFIX_FEEDBACK;
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
 * Provides feedback for a student in a session.
 */
public class AttendanceFeedbackCommand extends SessionCommand {

    public static final String COMMAND_WORD = "feedback";
    public static final String COMMAND_STRING = SessionCommand.COMMAND_STRING + " " + COMMAND_WORD;

    public static final String MESSAGE_USAGE = COMMAND_STRING
            + ": Provides feedback for a student in a session. "
            + "\nParameters: STUDENT_IDENTIFIER "
            + PREFIX_SESSION + "SESSION_ID "
            + PREFIX_FEEDBACK + "FEEDBACK"
            + "\nExample: " + COMMAND_STRING + " 1 "
            + PREFIX_SESSION + "2 "
            + PREFIX_FEEDBACK + "Good job!";

    public static final String MESSAGE_SUCCESS = "Feedback updated for %1$s in Session: %2$s";
    public static final String MESSAGE_RECORD_NOT_FOUND = "%1$s is not assigned to Session: %2$s";

    private final Identity identity;
    private final int sessionId;
    private final Feedback feedback;

    /**
     * Creates an AttendanceFeedbackCommand for the given {@code identity} and {@code Session}
     */
    public AttendanceFeedbackCommand(Identity identity, int sessionId, Feedback feedback) {
        this.identity = requireNonNull(identity);
        this.sessionId = sessionId;
        this.feedback = requireNonNull(feedback);
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

        AttendanceRecord dummyRecord = new AttendanceRecord(person.get().getId(), sessionId, true, Feedback.empty());
        Optional<AttendanceRecord> existingRecord = model.findAttendanceRecord(dummyRecord);
        if (existingRecord.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_RECORD_NOT_FOUND,
                    person.get().getName().fullName, Messages.format(session.get())));
        }

        AttendanceRecord record = new AttendanceRecord(person.get().getId(), sessionId,
                existingRecord.get().getAttendance(), feedback);

        model.setAttendanceRecord(existingRecord.get(), record);
        model.updateFilteredSessionList(Model.FILTER_SHOW_ALL_SESSIONS);
        return new CommandResult.Builder(
                String.format(MESSAGE_SUCCESS, person.get().getName().fullName, Messages.format(session.get())))
                .withTab(Tab.session())
                .withReverseCommand(new AttendanceFeedbackCommand(identity, sessionId,
                        existingRecord.get().getFeedback()))
                .build();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AttendanceFeedbackCommand otherCommand)) {
            return false;
        }
        return identity.equals(otherCommand.identity)
                && sessionId == otherCommand.sessionId
                && feedback.equals(otherCommand.feedback);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("identity", identity)
                .add("sessionId", sessionId)
                .add("feedback", feedback)
                .toString();
    }
}
