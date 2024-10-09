package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_DATE_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMINDER;

import java.time.format.DateTimeParseException;

import seedu.address.logic.commands.ReminderCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code ReminderCommand} object
 */
public class ReminderCommandParser implements Parser<ReminderCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the {@code ReminderCommand}
     * and return a new {@code ReminderCommand} object for execution.
     *
     * @param args The user input arguments to be parsed.
     * @return A {@code ReminderCommand} created from the parsed input.
     * @throws ParseException If the user input does not conform to the expected format.
     */
    @Override
    public ReminderCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_DATE, PREFIX_REMINDER);
        try {
            String name = argMultimap.getPreamble();
            String appointmentDate = argMultimap.getValue(PREFIX_DATE).orElse("");
            String reminderTime = argMultimap.getValue(PREFIX_REMINDER).orElse("");
            return new ReminderCommand(name, appointmentDate, reminderTime);
        } catch (DateTimeParseException e) {
            throw new ParseException(MESSAGE_INVALID_DATE_FORMAT);
        }
    }
}
