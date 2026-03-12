package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 *
 */
public class GitHub {
    public static final String MESSAGE_CONSTRAINTS =
            "GitHub username must only contain alphaneumeric characters or hyphens, "
            + "and cannot start or end with a hyphen.";

    public static final String VALIDATION_REGEX = "[a-zA-Z0-9]([a-zA-Z0-9]|-(?=[a-zA-Z0-9])){0,38}";

    public final String value;

    public GitHub(String github) {
        requireNonNull(github);
        checkArgument(isValidGitHub(github), MESSAGE_CONSTRAINTS);
        this.value = github;
    }

    public static boolean isValidGitHub(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof GitHub && value.equals(((GitHub) other).value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
