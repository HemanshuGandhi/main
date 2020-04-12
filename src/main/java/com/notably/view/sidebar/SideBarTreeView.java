package com.notably.view.sidebar;

import com.notably.commons.path.AbsolutePath;
import com.notably.model.block.Block;
import com.notably.model.block.BlockTree;
import com.notably.model.block.BlockTreeItem;
import com.notably.view.ViewPart;

import javafx.beans.property.Property;
import javafx.event.EventDispatcher;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;

/**
 * Represents the View-Controller for the SideBarTreeView component.
 */
public class SideBarTreeView extends ViewPart<Region> {

    private static final String FXML = "SideBarTreeView.fxml";
    private BlockTreeItem currentlyOpenedNote;

    @FXML
    private TreeView<Block> sideBarTreeView;

    public SideBarTreeView(BlockTree blockTree, Property<AbsolutePath> currentlyOpenPathProperty) {
        super(FXML);
        this.currentlyOpenedNote = blockTree.get(currentlyOpenPathProperty.getValue());
        autoUpdateTree(blockTree, currentlyOpenPathProperty);
        initializeTree();
    }

    /**
     * Listens to changes in the structure of the tree in the Model, and accordingly updates the
     * currently opened Note in the SidebarTreeView.
     * @param blockTree A custom object that represents the tree-like hierarchy of Notes.
     * @param currentlyOpenPathProperty The path to the currently opened Note.
     */
    private void autoUpdateTree(BlockTree blockTree, Property<AbsolutePath> currentlyOpenPathProperty) {
        currentlyOpenPathProperty.addListener((observable, oldValue, newValue) -> {
            this.currentlyOpenedNote = blockTree.get(AbsolutePath.fromString(newValue.toString()));
            initializeTree();
        });
    }

    /**
     * Calls helper functions that set the tree settings and populate the SideBarTreeView with data.
     */
    private void initializeTree() {
        setTreeStructure();
        setTreeSettings();
    }

    /**
     * Provides an alternative way to specify TreeView settings, versus via fxml tags.
     */
    private void setTreeSettings() {
        sideBarTreeView.setEditable(false);

        EventDispatcher sideBarEventDispatcher = sideBarTreeView.getEventDispatcher();
        sideBarTreeView.setEventDispatcher(new TreeCellEventDispatcher(sideBarEventDispatcher));
    }

    /**
     * Calls helper functions that set the strategies for populating the SideBarTreeView with data.
     */
    private void setTreeStructure() {
        sideBarTreeView.setCellFactory(treeView -> new SideBarTreeViewCell(currentlyOpenedNote));
        useLevelDisplayStrategy();
        useExpansionStrategy();
    }

    /**
     * Configures the SideBarTreeView to, when possible, display a 3-level nested hierarchy of Notes.
     * ie (Parent -> currently opened Note and its siblings -> direct children of currently opened Note).
     *
     * However, if the currently open Note is the default root Note, the Root is hidden and the
     * hierarchy is modified to look like: (Root's direct children -> Root's direct grandchildren).
     */
    private void useLevelDisplayStrategy() {
        TreeItem<Block> treeItem = this.currentlyOpenedNote.getTreeItem();
        TreeItem<Block> treeParent = treeItem.getParent();

        Boolean isRootOpen = treeParent == null;

        if (isRootOpen) {
            useRootNoteOpenStrategy(treeItem);
        } else {
            useNormalNoteOpenStrategy(treeParent);
        }
    }

    /**
     * Helper function that sets the display strategy for the case that the currently open Note
     * is a root Note, as per {@link #useLevelDisplayStrategy()}.
     *
     * @param treeItem the currently open Note.
     */
    private void useRootNoteOpenStrategy(TreeItem<Block> treeItem) {
        sideBarTreeView.setShowRoot(false);
        sideBarTreeView.setRoot(treeItem);
    }

    /**
     * Helper function that sets the display strategy for the case that the currently open Note
     * is not a root Note, as per {@link #useLevelDisplayStrategy()}
     *
     * @param treeParent the parent of the currently open Note.
     */
    private void useNormalNoteOpenStrategy(TreeItem<Block> treeParent) {
        Boolean isParentRoot = treeParent.getParent() == null;
        if (!isParentRoot) {
            sideBarTreeView.setShowRoot(true);
        } else {
            sideBarTreeView.setShowRoot(false);
        }
        sideBarTreeView.setRoot(treeParent);
    }

    /**
     * Expands the first and second level nodes, and contracts the third level nodes as per the
     * 3-level Display Strategy of {@link #useLevelDisplayStrategy()}.
     */
    private void useExpansionStrategy() {
        sideBarTreeView.getRoot().setExpanded(true);
        sideBarTreeView.getRoot().getChildren()
                .forEach(child -> {
                    child.setExpanded(true);
                    child.getChildren().forEach(grandChild -> {
                        grandChild.setExpanded(false);
                    });
                });
    }
}
