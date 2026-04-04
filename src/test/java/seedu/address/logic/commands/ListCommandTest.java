package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.enterDefaultEvent;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalPersons.getTypicalPersons;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.event.Event;
import seedu.address.model.event.EventDate;
import seedu.address.model.event.EventName;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_inGlobalMode_showsAllGlobalPersonsAndEventParticipants() throws CommandException {
        Event event = new Event(
                new EventName("Networking Night"),
                new EventDate("2026-10-01"),
                Optional.empty(),
                Optional.empty());
        event.addParticipant(AMY);
        model.addEvent(event);
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        CommandResult result = new ListCommand().execute(model);

        assertEquals(ListCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertFalse(model.isInEventParticipantsMode());
        assertTrue(model.isShowingGlobalPersonList());
        assertEquals(getTypicalPersons().size() + 1, model.getFilteredPersonList().size());
        assertTrue(model.getFilteredPersonList().contains(AMY));
    }

    @Test
    public void execute_inEventMode_showsAllEventPersons() throws CommandException {
        enterDefaultEvent(model);
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        CommandResult result = new ListCommand().execute(model);

        assertEquals(ListCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertTrue(model.isInEventParticipantsMode());
        assertFalse(model.isShowingGlobalPersonList());
        assertEquals(model.getAddressBook().getPersonList(), model.getFilteredPersonList());
    }
}
