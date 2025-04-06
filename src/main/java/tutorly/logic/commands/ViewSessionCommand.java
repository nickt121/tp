package tutorly.logic.commands;

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

    private final int sessionId;

    public ViewSessionCommand(int sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Session session = model.getSessionById(sessionId)
                .orElseThrow(() -> new CommandException(Messages.MESSAGE_SESSION_NOT_FOUND));

        return new CommandResult.Builder(String.format(Messages.MESSAGE_SESSION_SHOWN, Messages.format(session)))
                .withTab(Tab.session(session))
                .build();
    }

}
