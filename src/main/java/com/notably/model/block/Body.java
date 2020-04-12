package com.notably.model.block;

import static java.util.Objects.requireNonNull;

/**
 * Represents a {@code Block}'s body in Notably.
 * Guarantees: immutable; is non-null
 */
public class Body {
    public final String blockBody;

    /**
     * Constructs a {@code Body}.
     *
     * @param body A valid body text string.
     */
    public Body(String body) {
        requireNonNull(body);
        blockBody = body.trim();
    }

    /**
     * Returns a {@code String} of the {@code Body} contents.
     *
     * @return Body text string.
     */
    public String getText() {
        return this.blockBody;
    }
}
