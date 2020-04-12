package com.notably.view.suggestion;

import com.notably.model.suggestion.SuggestionItem;

import javafx.scene.control.ListCell;

/**
 * Custom {@code ListCell} that displays the graphics of a {@code SuggestionItem}
 * within {@code SuggestionsWindowView}.
 *
 * The layout of the components within a {@code SuggestionsListViewCell} is managed by
 * another custom View-Controller, {@link SuggestionsListCellData}.
 */
public class SuggestionsListViewCell extends ListCell<SuggestionItem> {
    @Override
    public void updateItem(SuggestionItem suggestionItem, boolean empty) {
        super.updateItem(suggestionItem, empty);

        if (empty || suggestionItem == null) {
            setText("");
            setGraphic(null);
        } else {
            SuggestionsListCellData data = new SuggestionsListCellData(suggestionItem, isSelected());
            setGraphic(data.getHBox());
        }
    }
}
