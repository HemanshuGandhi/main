package com.notably.model.block;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.notably.commons.path.AbsolutePath;
import com.notably.model.block.exceptions.DuplicateBlockException;
import com.notably.model.block.exceptions.NoSuchBlockException;

import javafx.scene.control.TreeItem;

/**
 * Implementation class of {@link BlockTreeItem}.
 */
public class BlockTreeItemImpl implements BlockTreeItem {
    private TreeItem<Block> blockTreeItem;

    /**
     * Creates a new {@link BlockTreeItem} implementation object.
     *
     * @param block {@link Block} to encapsulate
     */
    public BlockTreeItemImpl(Block block) {
        requireNonNull(block);
        blockTreeItem = new TreeItem<Block>(block);
    }

    /**
     * Creates a new {@link BlockTreeItem} implementation object.
     *
     * @param treeItem {@code TreeItem<Block>} to encapsulate
     */
    public BlockTreeItemImpl(TreeItem<Block> treeItem) {
        requireNonNull(treeItem);
        blockTreeItem = treeItem;
    }

    /**
     * Static method to create a root {@code BlockTreeItem}
     *
     * @return Root {@link BlockTreeItem}
     */
    public static BlockTreeItem createRootBlockTreeItem() {
        Block root = BlockImpl.createRootBlock();
        TreeItem<Block> rootTreeItem = new TreeItem<Block>(root);
        rootTreeItem.leafProperty().addListener((observable, oldValue, newValue) -> {
            Body rootBody = newValue
                ? new Body("Create a new note to get started!")
                : new Body("Open a note to get started!");
            rootTreeItem.setValue(new BlockImpl(rootTreeItem.getValue().getTitle(), rootBody));
        });
        return new BlockTreeItemImpl(rootTreeItem);
    }

    @Override
    public Title getTitle() {
        return blockTreeItem.getValue().getTitle();
    }

    @Override
    public Body getBody() {
        return blockTreeItem.getValue().getBody();
    }

    @Override
    public TreeItem<Block> getTreeItem() {
        return this.blockTreeItem;
    }

    @Override
    public Block getBlock() {
        return blockTreeItem.getValue();
    }

    @Override
    public BlockTreeItem getBlockParent() {
        if (blockTreeItem.getParent() == null) {
            return this;
        }
        return new BlockTreeItemImpl(blockTreeItem.getParent());
    }

    @Override
    public List<BlockTreeItem> getBlockChildren() {
        return blockTreeItem.getChildren().stream()
            .map(child -> new BlockTreeItemImpl(child)).collect(Collectors.toList());
    }

    @Override
    public void setBlockChildren(List<BlockTreeItem> newChildren) {
        requireNonNull(newChildren);
        List<TreeItem<Block>> convertedNewChildren = newChildren.stream()
            .map(newChild -> newChild.getTreeItem())
            .collect(Collectors.toList());
        blockTreeItem.getChildren().setAll(convertedNewChildren);
    }

    @Override
    public BlockTreeItem getBlockChild(Title title) throws NoSuchBlockException {
        requireNonNull(title);
        BlockTreeItem child = getBlockChildren()
            .stream()
            .filter(blockTreeItem -> blockTreeItem
                .getTitle()
                .equals(title))
            .findFirst()
            .orElseThrow(() -> new NoSuchBlockException(title.getText()));
        return child;
    }

    @Override
    public void setBlockChild(Title title, Block newBlock) {
        requireNonNull(title);
        requireNonNull(newBlock);
        BlockTreeItem child = getBlockChild(title);
        child.getTreeItem().setValue(newBlock);
    }

    @Override
    public void addBlockChild(Block block) throws DuplicateBlockException {
        requireNonNull(block);
        boolean hasMatchingChild = getBlockChildren()
            .stream()
            .anyMatch(child -> child
                .getTitle()
                .equals(block.getTitle()));
        if (hasMatchingChild) {
            throw new DuplicateBlockException();
        }
        blockTreeItem.getChildren().add(new TreeItem<Block>(block));
    }

    @Override
    public void removeBlockChild(Block toRemove) {
        requireNonNull(toRemove);
        TreeItem<Block> itemToRemove = getBlockChild(toRemove.getTitle()).getTreeItem();
        blockTreeItem.getChildren().remove(itemToRemove);
    }

    @Override
    public AbsolutePath getAbsolutePath() {
        ArrayList<String> blockPath = new ArrayList<>();
        BlockTreeItem current = this;
        while (!current.isRootBlock()) {
            blockPath.add(current.getTitle().getText());
            current = current.getBlockParent();
        }
        Collections.reverse(blockPath);
        return AbsolutePath.fromComponents(blockPath);
    }

    @Override
    public boolean isRootBlock() {
        return getTitle()
            .getText()
            .equals("Root")
            && getTreeItem().getParent() == null;
    }

    @Override
    public boolean equals(Object obj) {
        if (blockTreeItem == obj) {
            return true;
        }

        if (!(obj instanceof BlockTreeItem)) {
            return false;
        }

        BlockTreeItem otherBlock = (BlockTreeItem) obj;

        if (this.getBlockChildren().size() != otherBlock.getBlockChildren().size()) {
            return false;
        }

        for (int childIndex = 0; childIndex < this.getBlockChildren().size(); childIndex++) {
            if (!this.getBlockChildren().get(childIndex).equals(otherBlock.getBlockChildren().get(childIndex))) {
                return false;
            }
        }

        return this.getTitle().getText().equals(otherBlock.getTitle().getText());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle().getText(), getBlockChildren());
    }

}
