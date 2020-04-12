package com.notably.view;

import java.util.logging.Logger;

import com.notably.commons.GuiSettings;
import com.notably.commons.LogsCenter;
import com.notably.logic.Logic;
import com.notably.logic.commands.exceptions.CommandException;
import com.notably.logic.parser.exceptions.ParseException;
import com.notably.model.Model;
import com.notably.view.blockcontent.BlockContent;
import com.notably.view.sidebar.SideBarTreeView;
import com.notably.view.suggestion.SuggestionsWindowView;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends ViewPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;
    private Model model;

    // Independent View parts residing in this View container
    private HelpWindow helpWindow;
    private SideBarTreeView sidebarTreeView;
    private BlockContent blockContent;
    private SuggestionsWindowView suggestionsWindowView;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private Label notablyLogo;

    @FXML
    private VBox mainWindow;

    @FXML
    private VBox sideBar;

    @FXML
    private StackPane sideBarPlaceholder;

    @FXML
    private VBox blockContentPlaceholder;

    @FXML
    private VBox suggestionsWindow;

    public MainWindow(Stage primaryStage, Logic logic, Model model) {
        super(FXML, primaryStage);

        this.primaryStage = primaryStage;
        this.logic = logic;
        this.model = model;

        setWindowDefaultSize(logic.getGuiSettings());
        initializeHelpWindow(model);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        CommandBox commandBox = new CommandBox(this::executeCommand, model.inputProperty());
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

        sidebarTreeView = new SideBarTreeView(model.getBlockTree(), model.currentlyOpenPathProperty());
        sideBarPlaceholder.getChildren().add(sidebarTreeView.getRoot());

        blockContent = new BlockContent(blockContentPlaceholder, logic, model);

        suggestionsWindowView = new SuggestionsWindowView(model.getSuggestions(),
                model.responseTextProperty());
        suggestionsWindow.getChildren().add(suggestionsWindowView.getRoot());
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());

        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }

        sideBar.setPrefWidth(guiSettings.getWindowWidth() * 0.25);

    }

    /**
     * Creates and returns the HelpWindow component. In the process, sets a listener to ensure
     * that the Help Window is opened when activated.
     * @param model
     */
    private void initializeHelpWindow(Model model) {
        helpWindow = new HelpWindow();

        model.helpOpenProperty().addListener((Observable observable) -> {
            if (model.isHelpOpen()) {
                handleHelp();
            }
            model.setHelpOpen(false);
        });
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }

    /**
     * Executes the command and returns the result.
     *
     * @see com.notably.logic.Logic#execute(String)
     */
    private void executeCommand(String commandText) throws CommandException, ParseException {
        try {
            logic.execute(commandText);
        } catch (CommandException | ParseException e) {
            logger.info("Invalid command: " + commandText);
            throw e;
        }
    }
}
