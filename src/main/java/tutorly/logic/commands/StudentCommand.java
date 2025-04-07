package tutorly.logic.commands;

import tutorly.logic.Messages;
import tutorly.logic.commands.exceptions.CommandException;
import tutorly.model.Model;
import tutorly.ui.Tab;

/**
 * Represents a command related to students.
 */
public class StudentCommand extends Command {

    public static final String COMMAND_WORD = "student";
    public static final String COMMAND_STRING = COMMAND_WORD;

    @Override
    public CommandResult execute(Model model) throws CommandException {
        return new CommandResult.Builder(Messages.MESSAGE_PERSONS_SHOWN).withTab(Tab.student()).build();
    }

}
