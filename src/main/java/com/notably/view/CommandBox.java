package com.notably.view;

import com.notably.logic.commands.exceptions.CommandException;
import com.notably.logic.parser.exceptions.ParseException;
import com.notably.view.suggestion.SuggestionsWindowView;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * The VIEW component that is responsible for receiving user command inputs.
 */
public class CommandBox extends ViewPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";

    private final CommandExecutor commandExecutor;

    @FXML
    private TextField commandTextField;

    public CommandBox(CommandExecutor commandExecutor, StringProperty stringProperty) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        Platform.runLater(() -> commandTextField.requestFocus());
        initializeListeners(stringProperty);
    }

    /**
     * Sets up weak and strong listeners for view-state management.
     * @param stringProperty A property that, upon method execution, reflects the data that
     * the user types in the model, and also reflects changes in the model to the text field.
     */
    private void initializeListeners(StringProperty stringProperty) {
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
        commandTextField.textProperty().bindBidirectional(stringProperty);
        commandTextField.focusedProperty().addListener((observable, unused, isNowFocused) -> {
            if (isNowFocused) {
                Platform.runLater(() -> commandTextField.selectEnd());
            }
        });
        setNavigationHandler();
    }

    /**
     * Listens for keystrokes that signify the event where the user navigates out of the CommandBox.
     * Gives control to the {@link SuggestionsWindowView}.
     */
    private void setNavigationHandler() {
        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (KeyCode.DOWN == event.getCode()) {
                Window mainStage = Stage.getWindows().stream().filter(Window::isShowing).findFirst().get();
                Node suggestionsList = mainStage.getScene().lookup("#suggestionsListPanel");
                if (suggestionsList.isVisible()) {
                    suggestionsList.requestFocus();
                }
            }
        });
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        try {
            commandExecutor.execute(commandTextField.getText());
            commandTextField.setText("");
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see com.notably.logic.Logic#execute(String)
         */
        void execute(String commandText) throws CommandException, ParseException;
    }

}
