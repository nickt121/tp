package tutorly.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import tutorly.commons.util.ToStringBuilder;
import tutorly.logic.Messages;
import tutorly.logic.commands.exceptions.CommandException;
import tutorly.model.Model;
import tutorly.model.person.Identity;
import tutorly.model.person.Person;
import tutorly.ui.Tab;

/**
 * Deletes a person identified by their ID or name from the address book.
 */
public class DeleteStudentCommand extends StudentCommand {

    public static final String COMMAND_WORD = "delete";
    public static final String COMMAND_STRING = StudentCommand.COMMAND_STRING + " " + COMMAND_WORD;

    public static final String MESSAGE_USAGE = COMMAND_STRING
            + ": Deletes the student identified by a STUDENT_IDENTIFIER (ID or full name)."
            + "\nParameters: STUDENT_IDENTIFIER"
            + "\nExample: " + COMMAND_STRING + " 1";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted student: %1$s";

    private final Identity identity;

    public DeleteStudentCommand(Identity identity) {
        this.identity = identity;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Optional<Person> toDelete = model.getPersonByIdentity(identity);
        if (toDelete.isEmpty()) {
            throw new CommandException(Messages.MESSAGE_PERSON_NOT_FOUND);
        }

        model.deletePerson(toDelete.get());
        model.updateFilteredPersonList(Model.FILTER_SHOW_ALL_PERSONS);
        return new CommandResult.Builder(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(toDelete.get())))
                .withTab(Tab.student())
                .withReverseCommand(new AddStudentCommand(toDelete.get()))
                .build();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteStudentCommand otherDeleteCommand)) {
            return false;
        }

        return identity.equals(otherDeleteCommand.identity);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("identity", identity)
                .toString();
    }
}
