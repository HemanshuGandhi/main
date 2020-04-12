package com.notably.view.blockcontent;

import static com.notably.commons.util.CollectionUtil.requireAllNonNull;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import com.notably.commons.LogsCenter;
import com.notably.commons.path.AbsolutePath;
import com.notably.logic.Logic;
import com.notably.logic.exceptions.EditBlockBodyException;
import com.notably.model.Model;
import com.notably.model.block.BlockTreeItem;
import com.notably.view.ViewPart;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * View-Controller for the Editable view of the currently open block's content.
 */
public class BlockContentEditView extends ViewPart<Stage> {

    private static final Logger logger = LogsCenter.getLogger(BlockContentEditView.class);
    private static final String FXML = "blockcontent/BlockContentEditView.fxml";
    private final Stage stage;
    private final Window parentStage;
    private final Logic logic;
    private final Model model;

    @FXML
    private TextArea blockContentTextArea;

    public BlockContentEditView(Logic logic, Model model) {
        this(new Stage(), logic, model);
    }

    private BlockContentEditView(Stage root, Logic logic, Model model) {
        super(FXML, root);

        requireAllNonNull(logic, model);

        this.stage = root;
        this.logic = logic;
        this.model = model;
        this.parentStage = Stage.getWindows().stream().filter(Window::isShowing).findFirst().get();
        setStageStyle();
        setChangeListeners();
        setInitialData();
    }

    /**
     * Customizes the appearance and attributes of the Block Edit Modal
     */
    private void setStageStyle() {
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initOwner(parentStage);
        stage.initModality(Modality.WINDOW_MODAL);
    }

    /**
     * Sets listeners that update the content in the Block Edit modal and toggle the visibility
     * and controls of the edit modal.
     */
    private void setChangeListeners() {
        setContentChangeListeners();
        setModalVisibilityListeners();
        setKeyboardListeners();
        setInitialDimensionListeners();
        setStageDimensionListeners();
    }

    /**
     * Sets listeners that update the text content in the Block Edit modal to that of the currently
     * opened note, and whenever there are changes to the {@link com.notably.model.block.BlockTree};
     */
    private void setContentChangeListeners() {
        model.currentlyOpenPathProperty().addListener(observable -> setText(model));
        model.getBlockTree().getRootBlock().getTreeItem()
                .addEventHandler(TreeItem.treeNotificationEvent(), event -> setText(model));
    }

    /**
     * Sets listeners that bring the Block Edit modal into view when required, and in doing so apply
     * certain stylings to the main app window.
     */
    private void setModalVisibilityListeners() {
        updateMainWindowStyle(true);
        model.blockEditableProperty().addListener((observable, unused, isBlockEditable) -> {
            if (isBlockEditable) {
                handleEdit();
                model.setBlockEditable(false);
            }
            updateMainWindowStyle(isBlockEditable);
        });
    }

    /**
     * Sets listeners to trigger changes in the visibility of the Edit modal when certain Keystrokes
     * are registered. Primarily, allows for the user to exit the Block Edit modal using the "ESC"
     * keyboard shortcut.
     */
    private void setKeyboardListeners() {
        stage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ESCAPE == event.getCode()) {
                handleClose();
            }
        });
    }

    /**
     * Sets listeners that correctly initialise the X and Y coordinates of the Block Edit modal such
     * that it is centered to the main app window.
     */
    private void setInitialDimensionListeners() {
        ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
            setXDisplacement();
        };
        ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
            setYDisplacement();
        };
        stage.widthProperty().addListener(widthListener);
        stage.heightProperty().addListener(heightListener);

        stage.setOnShown(e -> {
            stage.widthProperty().removeListener(widthListener);
            stage.heightProperty().removeListener(heightListener);
        });
    }

    /**
     * Sets listeners that change the dimensions of the Block Edit modal upon being repeatedly
     * opened and closed.
     */
    private void setStageDimensionListeners() {
        setXDisplacement();
        setYDisplacement();
        stage.setOnShowing(event -> {
            setStageDimensionListeners();
        });
    }

    /**
     * Sets up the view's initial data and wire up all required change listeners.
     */
    private void setInitialData() {
        setText(model);
    }

    /**
     * Sets the {@link TextArea}'s content to the currently open block's content in markdown form.
     *
     * @param model App's model
     */
    private void setText(Model model) {
        requireNonNull(model);

        AbsolutePath currentlyOpenPath = model.getCurrentlyOpenPath();
        BlockTreeItem currentlyOpenBlock = model.getBlockTree().get(currentlyOpenPath);

        String markdownBody = currentlyOpenBlock.getBody().getText();

        blockContentTextArea.setText(markdownBody);
    }

    /**
     * Closes the Block Edit modal after saving the changes to the note's contents.
     */
    @FXML
    public void handleClose() {
        saveData();
        hide();
    }

    /**
     * Opens the Block Edit modal or focuses on it if it's already opened.
     */
    private void handleEdit() {
        if (!isShowing()) {
            show();
        } else {
            focus();
        }
    }

    /**
     * Applies a darkening effect to the main app window, whenever the Block Edit modal
     * is currently being displayed.
     *
     * @param isBlockEditable A boolean that is true if the currently open note is editable,
     * ie. the Block Edit modal is to be shown, false otherwise.
     */
    private void updateMainWindowStyle(Boolean isBlockEditable) {
        Node mainWindow = parentStage.getScene().lookup("#mainWindow");
        ChangeListener<Boolean> focusedListener = (observable, unused, isFocused) -> {
            if (!isFocused && stage.isShowing()) {
                ColorAdjust colorAdjust = new ColorAdjust();
                colorAdjust.setBrightness(-0.4);
                mainWindow.setEffect(colorAdjust);
            } else {
                mainWindow.setEffect(null);
            }
        };

        if (isBlockEditable) {
            parentStage.focusedProperty().addListener(focusedListener);
        } else {
            parentStage.focusedProperty().removeListener(focusedListener);
        }
    }

    /**
     * Shows the Block Edit Modal.
     *
     * @throws IllegalStateException <ul>
     *                               <li>
     *                               if this method is called on a thread other than the JavaFX
     *                               Application Thread.
     *                               </li>
     *                               <li>
     *                               if this method is called during animation or layout processing.
     *                               </li>
     *                               <li>
     *                               if this method is called on the primary stage.
     *                               </li>
     *                               <li>
     *                               if {@code dialogStage} is already showing.
     *                               </li>
     *                               </ul>
     */
    public void show() {
        logger.info("Showing Edit Modal for currently open note...");
        getRoot().show();
    }

    /**
     * Returns true if the Block Edit modal is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the Block Edit modal.
     */
    public void hide() {
        getRoot().hide();
        logger.info("Closing Edit Modal for currently open note.");
    }

    /**
     * Focuses on the Block Edit modal.
     */
    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Saves the data in the Block Edit Modal by notifying the {@link Logic} layer of the app.
     */
    private void saveData() {
        try {
            logic.editCurrentBlockBody(blockContentTextArea.getText());
            logger.info("Writing new Note's contents to file...");
        } catch (EditBlockBodyException e) {
            logger.warning("Unable to write new contents to file.");
        }
    }

    /**
     * Calculates and sets the width of the Block Edit modal with respect to the main app window.
     */
    private void setXDisplacement() {
        Double newWidth = parentStage.getWidth() * 0.8;
        stage.setWidth(newWidth);
        stage.setX(parentStage.getX() + parentStage.getWidth() / 2 - newWidth / 2);
    }

    /**
     * Calculates and sets the height of the Block Edit modal with respect to the main app window.
     */
    private void setYDisplacement() {
        Double newHeight = parentStage.getHeight() * 0.7;
        stage.setHeight(newHeight);
        stage.setY(parentStage.getY() + parentStage.getHeight() / 3 - newHeight / 3);
    }
}

