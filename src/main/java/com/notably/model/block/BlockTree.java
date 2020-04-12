package com.notably.model.block;

import com.notably.commons.path.AbsolutePath;

/**
 * API of {@link BlockTree} component.
 *
 * Custom tree-like data structure that uses the {@link AbsolutePath} object
 * to obtain the {@link BlockTreeItem} needed for manipulation.
 */
public interface BlockTree {
    /**
     * Gets the root {@link BlockTreeItem} of the {@link BlockTree}.
     *
     * @return Root {@link BlockTreeItem}
     */
    BlockTreeItem getRootBlock();

    /**
     * Gets the {@link BlockTreeItem} at the {@code path} specified.
     *
     * @param path {@link AbsolutePath} of the target block
     * @return {@link BlockTreeItem} at the path, if it exists
     */
    BlockTreeItem get(AbsolutePath path);

    /**
     * Adds a new {@link Block} to the block at the {@code path} specified.
     *
     * @param path {@link AbsolutePath} of the target {@link Block}
     * @param newBlock {@link Block} to be added to {@code path}
     */
    void add(AbsolutePath path, Block newBlock);

    /**
     * Sets the existing {@link Block} at the specified {@code path}, to {@code newBlock}.
     *
     * @param path {@link AbsolutePath} of the target {@link Block}
     * @param newBlock {@link Block} to be changed for {@code path}
     */
    void set(AbsolutePath path, Block newBlock);

    /**
     * Remove the {@link Block} at the specified {@code path}.
     *
     * @param path {@link AbsolutePath} of the target {@link Block}
     */
    void remove(AbsolutePath path);

}
