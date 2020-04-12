package com.notably.view.suggestion;

import static com.notably.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Optional;

import com.notably.model.suggestion.SuggestionItem;
import com.notably.view.ViewPart;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 * Custom View-Controller for the components within a {@code SuggestionsListViewCell}.
 */
public class SuggestionsListCellData extends ViewPart<Region> {

    private static final String FXML = "suggestion/SuggestionsListViewCell.fxml";

    @FXML
    private HBox container;

    @FXML
    private Label pathLabel;

    @FXML
    private Label selectedItemLabel;

    @FXML
    private Label searchFrequencyLabel;

    @FXML
    private Pane leftPlaceholderPane;

    @FXML
    private Pane rightPlaceholderPane;

    public SuggestionsListCellData(SuggestionItem suggestionItem, boolean isSelected) {
        super(FXML);
        requireAllNonNull(suggestionItem, isSelected);
        setPathLabel(suggestionItem);
        setSearchFrequencyLabel(suggestionItem);
        setSelectedItemLabel(suggestionItem, isSelected);
    }

    /**
     * Getter for the Root Component.
     * @return container of type {@code HBox}.
     */
    public HBox getHBox() {
        return container;
    }

    /**
     * Updates the pathLabel with the String representation of the {@code SuggestionItem's} path.
     *
     * @param item a particular {@code SuggestionItem} from the list of current suggestions.
     */
    private void setPathLabel(SuggestionItem item) {
        Optional<String> displayProperty = item.getProperty("displayText");
        String displayString = displayProperty.orElse("");
        pathLabel.setText(displayString);
    }

    /**
     * Updates the searchFrequencyLabel with the {@code SuggestionItem's} search count. If the current
     * list of suggestions was not generated for a search command, the label is set to an empty string.
     *
     * See {@link com.notably.logic.commands.suggestion.SearchSuggestionCommand}.
     *
     * @param item a particular {@code SuggestionItem} from the list of current suggestions.
     */
    private void setSearchFrequencyLabel(SuggestionItem item) {
        Optional<String> frequencyProperty = item.getProperty("frequency");
        String frequencyString = "";
        if (frequencyProperty.isPresent()) {
            frequencyString += frequencyProperty.get() + " matches in body";
        }
        searchFrequencyLabel.setText(frequencyString);
    }


    /**
     * Updates the selectedItemLabel with a prompt text based on the current command. The prompt
     * text is set to a non-empty String if the {@code SuggestionsListViewCell} is currently
     * selected, but empty otherwise.
     *
     * @param item a particular {@code SuggestionItem} from the list of current suggestions.
     */
    private void setSelectedItemLabel(SuggestionItem item, boolean isSelected) {
        if (isSelected) {
            rightPlaceholderPane.setMinWidth(30);
            selectedItemLabel.setVisible(true);
            Boolean isSearchCommand = item.getProperty("frequency").isPresent();
            if (isSearchCommand) {
                selectedItemLabel.setText(("Press enter to go to file"));
            } else {
                selectedItemLabel.setText("Press enter to autofill");
            }
        } else {
            selectedItemLabel.setVisible(false);
            rightPlaceholderPane.setMinWidth(0);
        }
    }
}
