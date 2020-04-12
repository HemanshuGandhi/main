package com.notably.model.block;

/**
 * API of the {@link Block} component.
 *
 * A {@link Block} is a data node containing a {@link Title} and a (optionally empty) {@link Body}.
 */
public interface Block {
    /**
     * Gets the {@link Title} of a {@link Block}.
     *
     * @return {@link Title} of the {@link Block}
     */
    Title getTitle();

    /**
     * Gets the {@link Body} content of a {@link Block}.
     *
     * @return {@link Body} of the {@link Block}
     */
    Body getBody();
}
