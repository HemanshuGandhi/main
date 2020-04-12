package com.notably.model.block;

import java.util.List;

import com.notably.commons.path.AbsolutePath;

import javafx.scene.control.TreeItem;

/**
 * API of the {@link BlockTreeItem} component.
 *
 * A {@link BlockTreeItem} is a single node in a {@link BlockTree}.
 * It contains a {@link Block} as well as information on its parent and children items.
 */
public interface BlockTreeItem extends Block {
    /**
     * Gets the {@code TreeItem<Block>} representation of the {@link BlockTreeItem}.
     *
     * @return {@code TreeItem<Block>} of the {@link BlockTreeItem}
     */
    TreeItem<Block> getTreeItem();

    /**
     * Gets the {@link Block} content contained in a {@link BlockTreeItem}.
     *
     * @return {@link Block} content stored
     */
    Block getBlock();

    /**
     * Gets the parent block of a block, if possible.
     * If attempting to get the parent of a root {@link Block}, it will just return itself.
     *
     * @return Parent {@link BlockTreeItem}
     */
    BlockTreeItem getBlockParent();

    /**
     * Gets a {@code List<BlockTreeItem>} of children blocks of the {@link BlockTreeItem}.
     *
     * @return List of the children
     */
    List<BlockTreeItem> getBlockChildren();

    /**
     * Replaces all the children of the {@link BlockTreeItem} with a new list of children.
     *
     * @param newChildren List of new children
     */
    void setBlockChildren(List<BlockTreeItem> newChildren);

    /**
     * Get the child of the {@link BlockTreeItem}, with the specified {@link Title}.
     *
     * @param title {@link Title} of the child
     * @return Child {@link BlockTreeItem}
     */
    BlockTreeItem getBlockChild(Title title);

    /**
     * Replaces the child of the {@link BlockTreeItem}, that matches the {@link Title}, with {@code newBlock}.
     *
     * @param title {@link Title} of the child
     * @param newBlock New {@link Block}
     */
    void setBlockChild(Title title, Block newBlock);

    /**
     * Adds a single new child {@code newBlock}, to the {@link BlockTreeItem}.
     *
     * @param newBlock New {@link Block}
     */
    void addBlockChild(Block newBlock);

    /**
     * Removes a specified child block from the {@link BlockTreeItem}.
     *
     * @param toRemove {@link Block} to remove
     */
    void removeBlockChild(Block toRemove);

    /**
     * Gets the absolute path of the current block in the tree.
     *
     * @return {@link AbsolutePath} of the {@link BlockTreeItem}
     */
    AbsolutePath getAbsolutePath();

    /**
     * Checks if a {@link BlockTreeItem} is root (has root characteristics).
     *
     * @return Whether the {@link BlockTreeItem} is root
     */
    boolean isRootBlock();
}
