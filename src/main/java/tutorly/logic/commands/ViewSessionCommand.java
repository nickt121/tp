package tutorly.logic.commands;

import java.util.Optional;

import tutorly.logic.Messages;
import tutorly.logic.commands.exceptions.CommandException;
import tutorly.model.Model;
import tutorly.model.session.Session;
import tutorly.ui.Tab;

/**
 * Shows a session.
 */
public class ViewSessionCommand extends SessionCommand {

    public static final String COMMAND_WORD = "view";
    public static final String COMMAND_STRING = SessionCommand.COMMAND_STRING + " " + COMMAND_WORD;
    public static final String MESSAGE_USAGE = COMMAND_STRING
            + ": Shows the session identified by a SESSION_ID."
            + "\nParameters: SESSION_ID"
            + "\nExample: " + COMMAND_STRING + " 1";

    private final Optional<Integer> sessionId;

    public ViewSessionCommand(int sessionId) {
        this.sessionId = Optional.of(sessionId);
    }

    public ViewSessionCommand() {
        this.sessionId = Optional.empty();
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        if (sessionId.isPresent()) {
            Session session = model.getSessionById(sessionId.get())
                    .orElseThrow(() -> new CommandException(Messages.MESSAGE_SESSION_NOT_FOUND));

            return new CommandResult.Builder(String.format(Messages.MESSAGE_SESSION_SHOWN, Messages.format(session)))
                    .withTab(Tab.session(session))
                    .build();
        }

        return new CommandResult.Builder(Messages.MESSAGE_SESSIONS_SHOWN).withTab(Tab.session()).build();
    }

}
