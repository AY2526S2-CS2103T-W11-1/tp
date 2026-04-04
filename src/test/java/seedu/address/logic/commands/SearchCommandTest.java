package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.EventBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.event.Event;
import seedu.address.model.event.EventDate;
import seedu.address.model.event.EventName;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code SearchCommand}.
 */
public class SearchCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate(Collections.singletonList("first"));
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate(Collections.singletonList("second"));

        SearchCommand searchFirstCommand = new SearchCommand(firstPredicate);
        SearchCommand searchSecondCommand = new SearchCommand(secondPredicate);

        // same object -> returns true
        assertTrue(searchFirstCommand.equals(searchFirstCommand));

        // same values -> returns true
        SearchCommand searchFirstCommandCopy = new SearchCommand(firstPredicate);
        assertTrue(searchFirstCommand.equals(searchFirstCommandCopy));

        // different types -> returns false
        assertFalse(searchFirstCommand.equals(1));

        // null -> returns false
        assertFalse(searchFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(searchFirstCommand.equals(searchSecondCommand));
    }

    @Test
    public void execute_zeroKeywords_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        NameContainsKeywordsPredicate predicate = preparePredicate(" ");
        SearchCommand command = new SearchCommand(predicate);
        expectedModel.showGlobalPersonList();
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
        assertTrue(model.isShowingGlobalPersonList());
        assertFalse(model.isInEventParticipantsMode());
    }

    @Test
    public void execute_inGlobalMode_searchesAggregatedGlobalPersons() {
        Event event = createEventWithParticipant(AMY);
        model.addEvent(event);
        expectedModel.addEvent(createEventWithParticipant(AMY));

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        NameContainsKeywordsPredicate predicate = preparePredicate("Amy");
        SearchCommand command = new SearchCommand(predicate);
        expectedModel.showGlobalPersonList();
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.singletonList(AMY), model.getFilteredPersonList());
        assertTrue(model.isShowingGlobalPersonList());
        assertFalse(model.isInEventParticipantsMode());
    }

    @Test
    public void execute_inEventMode_searchesOnlyCurrentEventPersons() {
        Event event = createEventWithParticipant(AMY);
        EventBook eventBook = new EventBook();
        eventBook.addEvent(event);

        model = new ModelManager(new AddressBookBuilder().withPerson(ALICE).build(), eventBook, new UserPrefs());
        expectedModel = new ModelManager(new AddressBookBuilder().withPerson(ALICE).build(),
                createEventBookWithParticipant(AMY), new UserPrefs());
        model.enterEvent(model.getFilteredEventList().get(0));
        expectedModel.enterEvent(expectedModel.getFilteredEventList().get(0));

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        NameContainsKeywordsPredicate predicate = preparePredicate("Alice Amy");
        SearchCommand command = new SearchCommand(predicate);

        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.singletonList(AMY), model.getFilteredPersonList());
        assertTrue(model.isInEventParticipantsMode());
        assertFalse(model.isShowingGlobalPersonList());
    }

    @Test
    public void execute_inGlobalMode_emailAndGitHubMatches() {
        Person alex = new PersonBuilder().withName("Alex Yeoh")
                .withEmail("alexyeoh@example.com")
                .withGitHub("alexyeoh")
                .build();
        Person david = new PersonBuilder().withName("David Li")
                .withEmail("lidavid@example.com")
                .withGitHub("lidavid")
                .build();
        Person charlotte = new PersonBuilder().withName("Charlotte Oliveiro")
                .withEmail("charlotte@example.com")
                .withGitHub(null)
                .build();
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(alex);
        addressBook.addPerson(david);
        addressBook.addPerson(charlotte);

        Model localModel = new ModelManager(addressBook, new UserPrefs());
        Model expectedLocalModel = new ModelManager(addressBook, new UserPrefs());
        NameContainsKeywordsPredicate predicate = preparePredicate("ALEXYEOH@EXAMPLE.COM LIDAVID");
        SearchCommand command = new SearchCommand(predicate);

        expectedLocalModel.showGlobalPersonList();
        expectedLocalModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, localModel,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2), expectedLocalModel);
        assertEquals(Arrays.asList(alex, david), localModel.getFilteredPersonList());
        assertTrue(localModel.isShowingGlobalPersonList());
    }

    @Test
    public void toStringMethod() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Arrays.asList("keyword"));
        SearchCommand searchCommand = new SearchCommand(predicate);
        String expected = SearchCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, searchCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new NameContainsKeywordsPredicate(Arrays.asList(userInput.split("\\s+")));
    }

    private Event createEventWithParticipant(Person participant) {
        Event event = new Event(
                new EventName("Networking Night"),
                new EventDate("2026-10-01"),
                Optional.empty(),
                Optional.empty());
        event.addParticipant(participant);
        return event;
    }

    private EventBook createEventBookWithParticipant(Person participant) {
        EventBook eventBook = new EventBook();
        eventBook.addEvent(createEventWithParticipant(participant));
        return eventBook;
    }
}
