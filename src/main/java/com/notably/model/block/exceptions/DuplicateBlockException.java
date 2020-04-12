package com.notably.model.block.exceptions;

/**
 * Signals that the operation will result in duplicate {@link Block} (Blocks are considered duplicates if they have the
 * same parent and title).
 */
public class DuplicateBlockException extends RuntimeException {
    public DuplicateBlockException() {
        super("Operation would result in duplicate Block");
    }
}
