package com.notably.model.block;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

/**
 * Implementation class of {@link Block}.
 */
public class BlockImpl implements Block {
    private Title title;
    private Body body;

    /**
     * Initializes a {@link Block} without the {@link Body}.
     * Used when creating a root block or a block without the optional body argument.
     *
     * @param title {@link Title} of the new {@link Block}
     */
    public BlockImpl(Title title) {
        this(title, new Body(""));
    }

    /**
     * Initializes a {@link Block} with the {@link Title} and {@link Body}.
     * Used when creating a block with additional body argument.
     *
     * @param title {@link Title} of the new {@link Block}
     * @param body {@link Body} of the new {@link Block}
     */
    public BlockImpl(Title title, Body body) {
        requireNonNull(title);
        requireNonNull(body);
        this.title = title;
        this.body = body;
    }

    /**
     * Static method to create a root {@link Block}.
     *
     * @return {@code Block} representing the root
     */
    public static Block createRootBlock() {
        return new BlockImpl(new Title("Root"), new Body("Create a new note to get started!"));
    }

    @Override
    public Title getTitle() {
        return this.title;
    }

    @Override
    public Body getBody() {
        return this.body;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Block // instanceof handles nulls
                && title.getText().equals(((Block) other).getTitle().getText())
                && body.getText().equals(((Block) other).getBody().getText())); // state check
    }

    @Override
    public int hashCode() {
        return Objects.hash(title.getText());
    }
}
