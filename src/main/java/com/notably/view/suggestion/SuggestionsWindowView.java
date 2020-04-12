package com.notably.view.suggestion;

import java.util.Optional;

import com.notably.model.suggestion.SuggestionItem;
import com.notably.view.CommandBox;
import com.notably.view.ViewPart;

import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;


/**
 * A View-Controller for the Suggestions List that appears in the View.
 */
public class SuggestionsWindowView extends ViewPart<Region> {

    private static final String FXML = "suggestion/SuggestionsWindowView.fxml";
    private static final int LIST_CELL_HEIGHT = 36;

    @FXML
    private VBox suggestionsBox;

    @FXML
    private Label responseText;

    @FXML
    private ListView<SuggestionItem> suggestionsListPanel;

    public SuggestionsWindowView(ObservableList<SuggestionItem> suggestionsList,
                                 Property<Optional<String>> responseText) {
        super(FXML);
        autoUpdateSuggestionsDisplay(suggestionsList, responseText);
        initializeSuggestionsList(suggestionsList);
    }

    /**
     * Sets listeners to update the visibility of the {@code SuggestionsWindow}, and its components.
     *
     * @param suggestionsList The current list of suggestions from the model.
     * @param responseTextProperty The info text to be displayed above the suggestions list, if any.
     */
    private void autoUpdateSuggestionsDisplay(ObservableList<SuggestionItem> suggestionsList,
                                              Property<Optional<String>> responseTextProperty) {
        responseTextProperty.addListener((observable, oldValue, newValue) -> {
            Optional<String> response = responseTextProperty.getValue();
            if (response.isPresent()) {
                responseText.setText(response.get());
                setResponseTextRenderingStatus(true);
            } else {
                setResponseTextRenderingStatus(false);
            }
        });

        suggestionsList.addListener((Observable observable) -> {
            suggestionsListPanel.setPrefHeight(computePrefListHeight(suggestionsList.size()));
            if (suggestionsList.size() == 0) {
                setSuggestionsListRenderingStatus(false);
            } else {
                setSuggestionsListRenderingStatus(true);
            }
        });
    }

    /**
     * Calls helper functions that set the List settings and populate the SideBarTreeView with data.
     */
    private void initializeSuggestionsList(ObservableList<SuggestionItem> suggestionsList) {
        suggestionsListPanel.setItems(suggestionsList);
        suggestionsListPanel.setCellFactory(listView -> new SuggestionsListViewCell());
        initializeSelectionHandlers();
    }

    /**
     * Sets up handlers that facilitate the selection of {@link SuggestionItem's}, and
     * facilitate navigation out of the Suggestions List.
     */
    private void initializeSelectionHandlers() {
        suggestionsListPanel.focusedProperty().addListener((observable, unused, isNowFocused) -> {
            if (isNowFocused && suggestionsListPanel.isVisible()) {
                suggestionsListPanel.getSelectionModel().select(0);
            }
        });

        suggestionsListPanel.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (KeyCode.ENTER == event.getCode()) {
                suggestionsListPanel.getSelectionModel().getSelectedItem().getAction().run();
                navigateOutOfList();
            }
            if (KeyCode.UP == event.getCode()) {
                int selectedIndex = suggestionsListPanel.getSelectionModel().getSelectedIndex();
                if (selectedIndex == 0) {
                    suggestionsListPanel.getSelectionModel().clearSelection();
                    navigateOutOfList();
                }
            }
        });
    }

    /**
     * Helper function that handles the event where the user navigates out of the Suggestions List.
     * Gives back control to the {@link CommandBox}.
     */
    private void navigateOutOfList() {
        Window mainStage = Stage.getWindows().stream().filter(Window::isShowing).findFirst().get();
        mainStage.getScene().lookup("#commandTextField").requestFocus();
    }

    /**
     * Toggles the visibility of the Suggestions List.
     * @param bool true if the Suggestions List should be visible, false otherwise.
     */
    private void setSuggestionsListRenderingStatus(boolean bool) {
        suggestionsListPanel.setManaged(bool);
        suggestionsListPanel.setVisible(bool);
    }

    /**
     * Toggles the visibility of the responseText component.
     * @param bool true if the responseText should be visible, false otherwise.
     */
    private void setResponseTextRenderingStatus(boolean bool) {
        responseText.setManaged(bool);
        responseText.setVisible(bool);
    }

    /**
     * Computes the height of the Suggestions List based on the number of items in the list.
     * This allows for the dynamic resizing of the list as the number of items change.
     *
     * @param listSize the number of items in the Suggestions List at a given time.
     * @return an int representing the height of the Suggestions List.
     */
    private int computePrefListHeight(int listSize) {
        return listSize * LIST_CELL_HEIGHT;
    }
}
