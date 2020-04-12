package com.notably.model.block;

import static com.notably.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents a {@code Block}'s title in Notably.
 * Guarantees: immutable; is valid as declared in {@link #isValidTitle(String)}
 */
public class Title {
    public static final String MESSAGE_CONSTRAINTS =
            "Titles should only contain alphanumeric characters, spaces and any of the following symbols: "
            + "!\"#$%&'()*+,.:;<=>?@[\\]^_`{|}~ "
            + "Titles should also not be blank. Title cannot start with a period character."
            + "No trailing or leading whitespaces.";

    /*
     * The first character of the title must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX =
        "([\\p{Alnum}\\p{Punct}&&[^\\/\\-\\.]]+[\\.\\s]+)*[\\p{Alnum}\\p{Punct}&&[^\\/\\-\\.]]+";

    public final String blockTitle;

    /**
     * Constructs a {@code Title}.
     *
     * @param title A valid title text string.
     */
    public Title(String title) {
        requireNonNull(title);
        checkArgument(isValidTitle(title), MESSAGE_CONSTRAINTS);
        blockTitle = title;
    }

    /**
     * Returns true if a given string is a valid title.
     *
     * @param test The title string to test.
     */
    public static boolean isValidTitle(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns a {@code String} of the {@code Title} content.
     *
     * @return Title text string.
     */
    public String getText() {
        return this.blockTitle;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Title // instanceof handles nulls
                && blockTitle.equalsIgnoreCase(((Title) other).blockTitle)); // state check
    }

    @Override
    public int hashCode() {
        return blockTitle.hashCode();
    }

}
