package tutorly.logic.parser;

import static tutorly.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tutorly.logic.parser.ParserUtil.parseSessionId;

import tutorly.logic.commands.ViewSessionCommand;
import tutorly.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ViewSessionCommand object
 */
public class ViewSessionCommandParser implements Parser<ViewSessionCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ViewSessionCommand
     * and returns a ViewSessionCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public ViewSessionCommand parse(String args) throws ParseException {
        try {
            int sessionId = parseSessionId(args.trim());
            return new ViewSessionCommand(sessionId);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewSessionCommand.MESSAGE_USAGE), pe);
        }
    }
}
