package tutorly.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import tutorly.commons.util.ToStringBuilder;
import tutorly.logic.Messages;
import tutorly.logic.commands.exceptions.CommandException;
import tutorly.model.Model;
import tutorly.model.session.Session;
import tutorly.ui.Tab;

/**
 * Deletes a session identified by its ID from the address book.
 */
public class DeleteSessionCommand extends SessionCommand {

    public static final String COMMAND_WORD = "delete";
    public static final String COMMAND_STRING = SessionCommand.COMMAND_STRING + " " + COMMAND_WORD;

    public static final String MESSAGE_USAGE = COMMAND_STRING
            + ": Deletes the session identified by a SESSION_ID."
            + "\nParameters: SESSION_ID"
            + "\nExample: " + COMMAND_STRING + " 1";

    public static final String MESSAGE_DELETE_SESSION_SUCCESS = "Deleted session: %1$s";

    private final int sessionId;

    public DeleteSessionCommand(int sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Optional<Session> toDelete = model.getSessionById(sessionId);
        if (toDelete.isEmpty()) {
            throw new CommandException(Messages.MESSAGE_INVALID_SESSION_ID);
        }

        model.deleteSession(toDelete.get());
        model.updateFilteredSessionList(Model.FILTER_SHOW_ALL_SESSIONS);
        return new CommandResult.Builder(String.format(MESSAGE_DELETE_SESSION_SUCCESS, Messages.format(toDelete.get())))
                .withTab(Tab.session())
                .withReverseCommand(new AddSessionCommand(toDelete.get()))
                .build();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof DeleteSessionCommand)) {
            return false;
        }

        DeleteSessionCommand otherCommand = (DeleteSessionCommand) other;
        return sessionId == otherCommand.sessionId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("sessionId", sessionId)
                .toString();
    }
}
