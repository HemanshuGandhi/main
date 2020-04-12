package com.notably.model.block;

import com.notably.commons.path.AbsolutePath;

import javafx.beans.property.Property;

/**
 * API of the {@link BlockModel} component.
 *
 * {@link BlockModel} is the entry point for the other components to interact and manipulate Notably's block data.
 */
public interface BlockModel {
    /**
     * Gets the {@link BlockTree} currently in use by Notably.
     *
     * @return Current {@link BlockTree}
     */
    BlockTree getBlockTree();

    /**
     * Replaces the content of the {@link BlockTree} with items from the new tree.
     *
     * @param blockTree Target {@link BlockTree} to copy the data from
     */
    void setBlockTree(BlockTree blockTree);

    /**
     * Replaces the data of the {@link BlockModel} with {@link newData}.
     *
     * @param newData Target {@link BlockModel} to copy the data from
     */
    void resetData(BlockModel newData);

    /**
     * Gets the {@link AbsolutePath} of the {@link Block} that is currently open.
     *
     * @return {@link AbsolutePath} of the currently open {@link Block}
     */
    AbsolutePath getCurrentlyOpenPath();

    /**
     * Gets the {@code Property<AbsolutePath>} of the block that is currently open.
     *
     * @return {@code Property<AbsolutePath>} of the currently open {@link Block}
     */
    Property<AbsolutePath> currentlyOpenPathProperty();

    /**
     * Checks if {@code path} exists in the {@link BlockModel}.
     *
     * @param path {@link AbsolutePath} to be checked
     * @return Whether {@code path} exists
     */
    boolean hasPath(AbsolutePath path);

    /**
     * Opens the {@link Block} at the specified {@code path}, if possible. (For open command)
     *
     * @param path {@link AbsolutePath} of the {@link Block} to be opened
     */
    void setCurrentlyOpenBlock(AbsolutePath path);

    /**
     * Adds a {@link Block} to the currently opened path, if possible. (For add command)
     *
     * @param block {@link Block} to be added
     */
    void addBlockToCurrentPath(Block block);

    /**
     * Removes the {@link Block} at the specified {@code path}, if possible. (For remove command)
     *
     * @param path {@link AbsolutePath} of the {@link Block} to be removed
     */
    void removeBlock(AbsolutePath path);

    /**
     * Sets the {@link Body} of the {@link Block} at the currently open path, to {@code newBody}. (For edit command)
     *
     * @param newBody New {@link Body} content
     */
    void updateCurrentlyOpenBlockBody(Body newBody);
}
