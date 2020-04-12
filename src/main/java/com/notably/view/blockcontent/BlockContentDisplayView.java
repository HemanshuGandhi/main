package com.notably.view.blockcontent;

import java.util.Objects;

import com.notably.commons.compiler.Compiler;
import com.notably.commons.path.AbsolutePath;
import com.notably.model.Model;
import com.notably.model.block.BlockTreeItem;
import com.notably.view.ViewPart;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

/**
 * Read-only and rendered view of the currently open block's content.
 */
public class BlockContentDisplayView extends ViewPart<WebView> {

    private static final String FXML = "blockcontent/BlockContentDisplayView.fxml";
    private final Model model;

    @FXML
    private VBox blockContentDisplay;

    @FXML
    private HBox blockTitlePathContainer;

    @FXML
    private Label blockTitleDisplay;

    @FXML
    private Pane separator;

    @FXML
    private Label blockPathDisplay;

    @FXML
    private WebView blockBodyDisplay;

    public BlockContentDisplayView(Model model) {
        super(FXML);
        Objects.requireNonNull(model);
        this.model = model;
        setup();
    }

    /**
     * Sets up the view's initial data and wires up all required change listeners.
     */
    private void setup() {
        updateBlockContentDisplay(model);
        setChangeListeners();
    }

    /**
     * Updates the content of the view-components to that of the currently open block.
     *
     * @param model App's model.
     */
    private void updateBlockContentDisplay(Model model) {
        Objects.requireNonNull(model);

        AbsolutePath currentlyOpenPath = model.getCurrentlyOpenPath();
        BlockTreeItem currentlyOpenBlock = model.getBlockTree().get(currentlyOpenPath);

        updateBlockTitleDisplay(currentlyOpenBlock);
        updateBlockPathDisplay(currentlyOpenPath);
        updateBlockBodyDisplay(currentlyOpenBlock);
    }

    /**
     * Sets the blockTitleDisplay's text to the currently open block's {@link com.notably.model.block.Title}.
     *
     * @param currentlyOpenBlock A {@link BlockTreeItem} representing the currently open note block.
     */
    private void updateBlockTitleDisplay(BlockTreeItem currentlyOpenBlock) {
        Objects.requireNonNull(currentlyOpenBlock);

        String titleString = currentlyOpenBlock.getTitle().getText();
        blockTitleDisplay.setText(titleString);
    }

    /**
     * Sets the blockPathDisplay's text to the currently open block's {@link AbsolutePath}.
     *
     * @param currentlyOpenPath A {@link BlockTreeItem} representing the currently open note block.
     */
    private void updateBlockPathDisplay(AbsolutePath currentlyOpenPath) {
        Objects.requireNonNull(currentlyOpenPath);

        String pathString = currentlyOpenPath.getStringRepresentation();
        blockPathDisplay.setText(pathString);
    }

    /**
     * Sets the blockBodyDisplay's text to the currently open block's {@link com.notably.model.block.Body}.
     *
     * @param currentlyOpenBlock A {@link BlockTreeItem} representing the currently open note block.
     */
    private void updateBlockBodyDisplay(BlockTreeItem currentlyOpenBlock) {
        Objects.requireNonNull(currentlyOpenBlock);

        String htmlBody = getHtmlBody(currentlyOpenBlock);
        blockBodyDisplay.getEngine().loadContent(htmlBody);
        blockBodyDisplay.getEngine().setUserStyleSheetLocation(getClass()
                .getResource("/view/blockcontent/BlockContentDisplay.css").toExternalForm());
    }

    /**
     * Helper function to retrieve the currently open block's {@link com.notably.model.block.Body}
     * rendered as HTML.
     *
     * @param currentlyOpenBlock A {@link BlockTreeItem} representing the currently open note block.
     * @return A String representing the note's body.
     */
    private String getHtmlBody(BlockTreeItem currentlyOpenBlock) {
        Objects.requireNonNull(currentlyOpenBlock);

        String markdownBody = currentlyOpenBlock.getBody().getText();
        String htmlBody = Compiler.compile(markdownBody);
        return htmlBody;
    }

    /**
     * Sets up listeners for View state management.
     */
    private void setChangeListeners() {
        model.currentlyOpenPathProperty().addListener(observable -> updateBlockContentDisplay(model));
        model.getBlockTree().getRootBlock().getTreeItem()
                .addEventHandler(TreeItem.treeNotificationEvent(), event -> updateBlockContentDisplay(model));
    }
}

