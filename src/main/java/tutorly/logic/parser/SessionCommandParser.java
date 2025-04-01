package tutorly.logic.parser;

import static tutorly.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import tutorly.logic.commands.AddSessionCommand;
import tutorly.logic.commands.AttendanceMarkSessionCommand;
import tutorly.logic.commands.AttendanceUnmarkSessionCommand;
import tutorly.logic.commands.Command;
import tutorly.logic.commands.DeleteSessionCommand;
import tutorly.logic.commands.EnrolSessionCommand;
import tutorly.logic.commands.ListSessionCommand;
import tutorly.logic.commands.SearchSessionCommand;
import tutorly.logic.commands.SessionCommand;
import tutorly.logic.commands.UnenrolSessionCommand;
import tutorly.logic.parser.exceptions.ParseException;

/**
 * Subparser for the session command.
 */
public class SessionCommandParser extends AddressBookParser {

    @Override
    protected Command parseCommand(String command, String args) throws ParseException {
        switch (command) {
        case ListSessionCommand.COMMAND_WORD:
            return new ListSessionCommand();

        case AddSessionCommand.COMMAND_WORD:
            return new AddSessionCommandParser().parse(args);

        case SearchSessionCommand.COMMAND_WORD:
            return new SearchSessionCommandParser().parse(args);

        case EnrolSessionCommand.COMMAND_WORD:
            return new EnrolSessionCommandParser().parse(args);

        case UnenrolSessionCommand.COMMAND_WORD:
            return new UnenrolSessionCommandParser().parse(args);

        case DeleteSessionCommand.COMMAND_WORD:
            return new DeleteSessionCommandParser().parse(args);

        case AttendanceMarkSessionCommand.COMMAND_WORD:
            return new AttendanceMarkSessionCommandParser().parse(args);

        case AttendanceUnmarkSessionCommand.COMMAND_WORD:
            return new AttendanceUnmarkSessionCommandParser().parse(args);

        default:
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

    @Override
    protected Command defaultCommand() {
        return new SessionCommand();
    }

}
