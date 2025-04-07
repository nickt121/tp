package tutorly.logic.commands;

import tutorly.logic.Messages;
import tutorly.logic.commands.exceptions.CommandException;
import tutorly.model.Model;
import tutorly.ui.Tab;

/**
 * Represents a command related to sessions.
 */
public class SessionCommand extends Command {

    public static final String COMMAND_WORD = "session";
    public static final String COMMAND_STRING = COMMAND_WORD;

    @Override
    public CommandResult execute(Model model) throws CommandException {
        return new CommandResult.Builder(Messages.MESSAGE_SESSIONS_SHOWN).withTab(Tab.session()).build();
    }

}
