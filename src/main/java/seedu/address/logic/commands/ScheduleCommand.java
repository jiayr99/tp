package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Schedule;

/**
 * Schedules an appointment for a person in the address book.
 * The command ensures that the appointment is within valid working hours
 * (weekdays from 9 AM to 5 PM) and that the selected time slot is available.
 */
public class ScheduleCommand extends Command {

    public static final String COMMAND_WORD = "schedule";
    public static final String MESSAGE_SUCCESS = "Scheduled %s for %s";
    public static final String MESSAGE_INVALID_TIME = "Scheduled time must be a weekday and"
            + "on the hour between 0900 and 1700";
    public static final String MESSAGE_SLOT_TAKEN = "The selected time slot is already taken.";
    public static final String MESSAGE_INVALID_NAME = "Person not found";
    private String name;
    private Schedule schedule;

    /**
     * Constructs a ScheduleCommand to schedule an appointment for the specified person.
     *
     * @param name The name of the person.
     * @param schedule The schedule date and time and optional notes.
     */
    public ScheduleCommand(String name, Schedule schedule) {
        this.name = name;
        this.schedule = schedule;
    }


    /**
     * Executes the schedule command.
     * Schedules an appointment for a person, ensuring that the time slot is available,
     * and the appointment is within valid working hours.
     *
     * @param model The model containing the list of persons.
     * @return CommandResult indicating success of scheduling.
     * @throws CommandException If the person's name is not found, the time slot is already taken,
     *                          or the scheduled time is invalid.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();
        int index = -1;
        for (int i = 0; i < lastShownList.size(); i++) {
            if (lastShownList.get(i).getName().toString().equals(name)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new CommandException(MESSAGE_INVALID_NAME);
        }

        // Check if the schedule time is valid (on the hour and within weekday working hours)
        if (!isOnTheHour(schedule.dateTime) || !isWithinWorkingHours(schedule.dateTime)) {
            throw new CommandException(MESSAGE_INVALID_TIME);
        }

        // Check if the time slot is already taken
        if (isTimeSlotTaken(lastShownList, schedule.dateTime)) {
            throw new CommandException(MESSAGE_SLOT_TAKEN);
        }

        Person personToEdit = lastShownList.get(index);
        Person editedPerson = new Person(
                personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                personToEdit.getAddress(), schedule, personToEdit.getReminder(), personToEdit.getTags());
        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_SUCCESS, this.schedule, name));
    }

    /**
     * Checks if the given LocalDateTime is on the hour (i.e., minutes == 0).
     */
    private boolean isOnTheHour(String dateTime) {
        LocalDateTime localDateTime = getLocalDateTime(dateTime);
        return localDateTime.getMinute() == 0;
    }

    /**
     * Checks if the given LocalDateTime falls on a weekday (Monday to Friday) between 9 AM and 5 PM.
     */
    private boolean isWithinWorkingHours(String dateTime) {
        LocalDateTime localDateTime = getLocalDateTime(dateTime);

        // Check if the day is a weekday
        DayOfWeek day = localDateTime.getDayOfWeek();
        boolean isWeekday = day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;

        // Check if the time is between 9 AM and 5 PM
        int hour = localDateTime.getHour();
        boolean isWorkingHours = hour >= 9 && hour < 17;

        return isWeekday && isWorkingHours;
    }

    private static LocalDateTime getLocalDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"));
    }

    /**
     * Checks if the time slot is already taken by another person.
     */
    private boolean isTimeSlotTaken(List<Person> personList, String dateTime) {
        for (Person person : personList) {
            Schedule schedule = person.getSchedule();
            if (schedule != null && schedule.dateTime.equals(dateTime)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object other) {
        // Short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ScheduleCommand)) {
            return false;
        }

        // State check
        ScheduleCommand otherCommand = (ScheduleCommand) other;
        return name.equals(otherCommand.name)
                && schedule.equals(otherCommand.schedule);
    }
}
