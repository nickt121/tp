package tutorly.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static tutorly.logic.commands.CommandTestUtil.assertCommandFailure;
import static tutorly.logic.commands.CommandTestUtil.assertCommandSuccess;
import static tutorly.testutil.TypicalAddressBook.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import tutorly.logic.Messages;
import tutorly.model.Model;
import tutorly.model.ModelManager;
import tutorly.model.UserPrefs;
import tutorly.model.session.Session;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteSessionCommand}.
 */
public class DeleteSessionCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validSessionId_success() {
        Session sessionToDelete = model.getSessionById(2).get();
        DeleteSessionCommand deleteCommand = new DeleteSessionCommand(2);

        String expectedMessage = String.format(DeleteSessionCommand.MESSAGE_DELETE_SESSION_SUCCESS,
                Messages.format(sessionToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deleteSession(sessionToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidSessionId_throwsCommandException() {
        int invalidSessionId = model.getFilteredSessionList().size() + 1;
        DeleteSessionCommand deleteCommand = new DeleteSessionCommand(invalidSessionId);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_SESSION_NOT_FOUND);
    }

    @Test
    public void equals() {
        int sessionIdFirst = 1;
        int sessionIdSecond = 2;

        DeleteSessionCommand deleteFirstCommand = new DeleteSessionCommand(sessionIdFirst);
        DeleteSessionCommand deleteSecondCommand = new DeleteSessionCommand(sessionIdSecond);

        // same object -> returns true
        assertEquals(deleteFirstCommand, deleteFirstCommand);

        // same values -> returns true
        DeleteSessionCommand deleteFirstCommandCopy = new DeleteSessionCommand(sessionIdFirst);
        assertEquals(deleteFirstCommand, deleteFirstCommandCopy);

        // different types -> returns false
        assertNotEquals(1, deleteFirstCommand);

        // null -> returns false
        assertNotEquals(null, deleteFirstCommand);

        // different session -> returns false
        assertNotEquals(deleteFirstCommand, deleteSecondCommand);
    }

    @Test
    public void toStringMethod() {
        int sessionId = 1;
        DeleteSessionCommand deleteCommand = new DeleteSessionCommand(sessionId);
        String expected = DeleteSessionCommand.class.getCanonicalName() + "{sessionId=" + sessionId + "}";
        assertEquals(expected, deleteCommand.toString());
    }
}
