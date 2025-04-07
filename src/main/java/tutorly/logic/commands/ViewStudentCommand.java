package tutorly.logic.commands;

import tutorly.logic.Messages;
import tutorly.logic.commands.exceptions.CommandException;
import tutorly.model.Model;
import tutorly.model.person.Identity;
import tutorly.model.person.Person;
import tutorly.ui.Tab;

/**
 * Shows a student.
 */
public class ViewStudentCommand extends StudentCommand {

    public static final String COMMAND_WORD = "view";
    public static final String COMMAND_STRING = StudentCommand.COMMAND_STRING + " " + COMMAND_WORD;
    public static final String MESSAGE_USAGE = COMMAND_STRING
            + ": Shows the student identified by a STUDENT_IDENTIFIER."
            + "\nParameters: STUDENT_IDENTIFIER"
            + "\nExample: " + COMMAND_STRING + " 1";

    private final Identity identity;

    public ViewStudentCommand(Identity identity) {
        this.identity = identity;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Person student = model.getPersonByIdentity(identity)
                .orElseThrow(() -> new CommandException(Messages.MESSAGE_PERSON_NOT_FOUND));

        model.updateFilteredPersonList(Model.FILTER_SHOW_ALL_PERSONS);
        return new CommandResult.Builder(String.format(Messages.MESSAGE_PERSON_SHOWN, Messages.format(student)))
                .withTab(Tab.student(student))
                .build();
    }

}
