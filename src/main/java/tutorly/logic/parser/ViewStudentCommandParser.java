package tutorly.logic.parser;

import static tutorly.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import tutorly.logic.commands.ViewStudentCommand;
import tutorly.logic.parser.exceptions.ParseException;
import tutorly.model.person.Identity;

/**
 * Parses input arguments and creates a new DeleteCommand object
 */
public class ViewStudentCommandParser implements Parser<ViewStudentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns a DeleteCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public ViewStudentCommand parse(String args) throws ParseException {
        try {
            Identity identity = ParserUtil.parseIdentity(args);
            return new ViewStudentCommand(identity);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewStudentCommand.MESSAGE_USAGE), pe);
        }
    }

}
