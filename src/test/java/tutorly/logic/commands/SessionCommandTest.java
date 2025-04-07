package tutorly.logic.commands;

import static tutorly.logic.commands.CommandTestUtil.assertCommandSuccess;
import static tutorly.testutil.TypicalAddressBook.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import tutorly.logic.Messages;
import tutorly.model.Model;
import tutorly.model.ModelManager;
import tutorly.model.UserPrefs;
import tutorly.ui.Tab;

public class SessionCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_noArguments_success() {
        CommandResult expectedCommandResult = new CommandResult.Builder(Messages.MESSAGE_SESSIONS_SHOWN)
                .withTab(Tab.session())
                .build();
        assertCommandSuccess(new SessionCommand(), model, expectedCommandResult, expectedModel);
    }

}
