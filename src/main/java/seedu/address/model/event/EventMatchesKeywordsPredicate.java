package seedu.address.model.event;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that an {@code Event}'s visible fields contain the full search phrase.
 */
public class EventMatchesKeywordsPredicate implements Predicate<Event> {
    private final List<String> keywords;
    private final String searchPhrase;

    /**
     * Creates a predicate that matches the given search terms as one normalised phrase.
     */
    public EventMatchesKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
        searchPhrase = keywords.stream()
                .map(String::trim)
                .filter(keyword -> !keyword.isEmpty())
                .collect(Collectors.joining(" "));
    }

    @Override
    public boolean test(Event event) {
        return !searchPhrase.isEmpty() && matchesAnyVisibleField(event, searchPhrase);
    }

    private boolean matchesAnyVisibleField(Event event, String keyword) {
        return searchableFields(event).anyMatch(field -> containsIgnoreCase(field, keyword));
    }

    private Stream<String> searchableFields(Event event) {
        return Stream.of(
                event.getName().fullName,
                event.getDate().toString(),
                event.getLocation().orElse(""),
                event.getDescription().orElse(""));
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        String normalizedKeyword = keyword.trim().toLowerCase(Locale.ROOT);
        return value.toLowerCase(Locale.ROOT).contains(normalizedKeyword);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof EventMatchesKeywordsPredicate)) {
            return false;
        }

        EventMatchesKeywordsPredicate otherPredicate = (EventMatchesKeywordsPredicate) other;
        return searchPhrase.equals(otherPredicate.searchPhrase);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
