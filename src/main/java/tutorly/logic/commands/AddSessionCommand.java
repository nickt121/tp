package tutorly.logic.commands;

import static java.util.Objects.requireNonNull;
import static tutorly.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static tutorly.logic.parser.CliSyntax.PREFIX_TIMESLOT;

import tutorly.commons.util.ToStringBuilder;
import tutorly.logic.Messages;
import tutorly.logic.commands.exceptions.CommandException;
import tutorly.model.Model;
import tutorly.model.session.Session;
import tutorly.ui.Tab;

/**
 * Creates a new tutoring session.
 */
public class AddSessionCommand extends SessionCommand {

    public static final String COMMAND_WORD = "add";
    public static final String COMMAND_STRING = SessionCommand.COMMAND_STRING + " " + COMMAND_WORD;

    public static final String MESSAGE_USAGE = COMMAND_STRING
            + ": Creates a tutoring session."
            + "\nParameters: "
            + PREFIX_TIMESLOT + "TIMESLOT "
            + PREFIX_SUBJECT + "SUBJECT"
            + "\nExample: " + COMMAND_STRING + " "
            + PREFIX_TIMESLOT + "30 Mar 2025 11:30-13:30 "
            + PREFIX_SUBJECT + "Mathematics";

    public static final String MESSAGE_SUCCESS = "New session created: %1$s";
    public static final String MESSAGE_DUPLICATE_SESSION = "This session already exists.";
    public static final String MESSAGE_LIMIT_REACHED = "Limit reached; cannot add any more sessions. "
            + "Use the clear command to reset.";

    private final Session toCreate;

    /**
     * Creates a CreateSessionCommand to add the specified {@code Session}.
     *
     * @param session The session to be created.
     */
    public AddSessionCommand(Session session) {
        requireNonNull(session);
        toCreate = session;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasOverlappingSession(toCreate)) {
            throw new CommandException(Messages.MESSAGE_SESSION_OVERLAP);
        } else if (model.hasSession(toCreate)) {
            throw new CommandException(MESSAGE_DUPLICATE_SESSION);
        }

        try {
            model.addSession(toCreate);
        } catch (IllegalStateException e) {
            throw new CommandException(MESSAGE_LIMIT_REACHED);
        }

        model.updateFilteredSessionList(Model.FILTER_SHOW_ALL_SESSIONS);
        return new CommandResult.Builder(String.format(MESSAGE_SUCCESS, Messages.format(toCreate)))
                .withTab(Tab.session(toCreate))
                .withReverseCommand(new DeleteSessionCommand(toCreate.getId()))
                .build();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddSessionCommand otherCommand)) {
            return false;
        }

        return toCreate.equals(otherCommand.toCreate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toCreate", toCreate)
                .toString();
    }
}
