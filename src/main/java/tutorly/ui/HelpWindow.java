package tutorly.ui;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import tutorly.commons.core.LogsCenter;

/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart<Stage> {

    public static final String USERGUIDE_URL = "https://ay2425s2-cs2103t-t17-3.github.io/tp/UserGuide.html";
    public static final String HELP_MESSAGE = "For more info, refer to the user guide: " + USERGUIDE_URL;
    public static final String COMMAND_SUMMARY = """
Command Summary:
    - General commands:
        - help: Shows this help message.
        - clear: Clears all students and sessions from the app.
        - exit: Closes the app.
        - undo: Undoes the last successfully executed command.

    - Viewing tabs:
        - student: Switches to the students tab.
        - session: Switches to the sessions tab.

    - Student management:
        - student add n/NAME [p/PHONE] [e/EMAIL] [a/ADDRESS] [m/MEMO] [t/TAG]…​: Adds a student.
        - student list: Lists all students.
        - student view STUDENT_IDENTIFIER: Scrolls to the details of the specified student.
        - student edit STUDENT_IDENTIFIER [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [m/MEMO] [t/TAG]…​ : \
Edits a student's details.
        - student search [ses/SESSION_ID] [n/NAME_KEYWORDS] [p/PHONE_KEYWORDS]: Searches for students.
        - student delete STUDENT_IDENTIFIER: Deletes a student.

    - Session management:
        - session add t/TIMESLOT sub/SUBJECT: Adds a session.
        - session list: Lists all sessions.
        - session view SESSION_ID: Shows the attendance for the specified session.
        - session edit SESSION_ID [t/TIMESLOT] [sub/SUBJECT]: Edits a session's details.
        - session search [d/DATE] [sub/SUBJECT_KEYWORDS]: Searches for sessions.
        - session delete SESSION_ID: Deletes a session.
        - session enrol STUDENT_IDENTIFIER ses/SESSION_ID: Enrols a student to a session.
        - session unenrol STUDENT_IDENTIFIER ses/SESSION_ID: Unenrols a student from a session.
        - session mark STUDENT_IDENTIFIER ses/SESSION_ID: Marks attendance for a student in a session.
        - session unmark STUDENT_IDENTIFIER ses/SESSION_ID: Unmarks attendance for a student in a session.
        - session feedback STUDENT_IDENTIFIER ses/SESSION_ID f/FEEDBACK: \
Adds or updates feedback for a student in a session.""";

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";

    @FXML
    private Label commandSummary;
    @FXML
    private Button copyButton;

    @FXML
    private Label helpMessage;

    /**
     * Creates a new HelpWindow.
     *
     * @param root Stage to use as the root of the HelpWindow.
     */
    public HelpWindow(Stage root) {
        super(FXML, root);
        commandSummary.setText(COMMAND_SUMMARY);
        helpMessage.setText(HELP_MESSAGE);
    }

    /**
     * Creates a new HelpWindow.
     */
    public HelpWindow() {
        this(new Stage());
    }

    /**
     * Shows the help window.
     * @throws IllegalStateException
     *     <ul>
     *         <li>
     *             if this method is called on a thread other than the JavaFX Application Thread.
     *         </li>
     *         <li>
     *             if this method is called during animation or layout processing.
     *         </li>
     *         <li>
     *             if this method is called on the primary stage.
     *         </li>
     *         <li>
     *             if {@code dialogStage} is already showing.
     *         </li>
     *     </ul>
     */
    public void show() {
        logger.fine("Showing help page about the application.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the help window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the help window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Copies the URL to the user guide to the clipboard.
     */
    @FXML
    private void copyUrl() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent url = new ClipboardContent();
        url.putString(USERGUIDE_URL);
        clipboard.setContent(url);
    }
}
