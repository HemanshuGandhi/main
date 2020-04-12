package com.notably.model.block.exceptions;

/**
 * Signals that the operation will modify a root {@link Block}, which is not allowed by design.
 */
public class CannotModifyRootException extends RuntimeException {
    public CannotModifyRootException() {
        super("Root item cannot be modified");
    }
}
