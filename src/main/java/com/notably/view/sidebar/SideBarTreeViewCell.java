package com.notably.view.sidebar;

import com.notably.model.block.Block;
import com.notably.model.block.BlockTreeItem;

import javafx.scene.control.TreeCell;

/**
 * Custom {@code TreeCell} that displays the text of a {@code Block} within {@link SideBarTreeView}.
 */
class SideBarTreeViewCell extends TreeCell<Block> {
    private BlockTreeItem currentlyOpenedNote;

    public SideBarTreeViewCell(BlockTreeItem currentlyOpenedNote) {
        super();
        this.currentlyOpenedNote = currentlyOpenedNote;
    }

    @Override
    protected void updateItem(Block block, boolean empty) {
        super.updateItem(block, empty);

        setGraphic(null);
        if (empty || block == null) {
            setText(null);
        } else {
            setText(getNoteTitle());
            setSelectedProperty(block);
        }
    }

    private String getNoteTitle() {
        return getItem().getTitle().getText();
    }

    private void setSelectedProperty(Block block) {
        if ((block == currentlyOpenedNote.getBlock())) {
            updateSelected(true);
        } else {
            updateSelected(false);
        }
    }
}
