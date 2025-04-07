package tutorly.logic.commands;

import static tutorly.logic.commands.CommandTestUtil.assertCommandFailure;
import static tutorly.logic.commands.CommandTestUtil.assertCommandSuccess;
import static tutorly.testutil.TypicalAddressBook.ALICE;
import static tutorly.testutil.TypicalAddressBook.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import tutorly.logic.Messages;
import tutorly.model.Model;
import tutorly.model.ModelManager;
import tutorly.model.UserPrefs;
import tutorly.model.person.Identity;
import tutorly.ui.Tab;

public class ViewStudentCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIdentity_success() {
        StudentCommand studentCommand = new ViewStudentCommand(new Identity(ALICE.getId()));
        CommandResult expectedCommandResult = new CommandResult.Builder(String.format(Messages.MESSAGE_PERSON_SHOWN,
                Messages.format(ALICE)))
                .withTab(Tab.student(ALICE))
                .build();
        assertCommandSuccess(studentCommand, model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_invalidIdentity_throwsCommandException() {
        assertCommandFailure(new ViewStudentCommand(new Identity(999)), model, Messages.MESSAGE_PERSON_NOT_FOUND);
    }

}
