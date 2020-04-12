package com.notably.model.viewstate;

import javafx.beans.property.StringProperty;

/**
 * API for the CommandInputModel component.
 *
 * Represents the data that the user sees as text within the text field of the CommandInputView
 * as well as some supporting operations.
 */
public interface CommandInputModel {

    /**
     * Represents the text data that the user currently sees within the text field of the CommandInputView.
     * @return A javafx StringProperty object.
     */
    StringProperty inputProperty();

    /**
     * Gets the text data saved in the CommandInputModel.
     * @return A string corresponding to the text data saved in the CommandInputModel.
     */
    String getInput();

    /**
     * Sets the data for the text in the CommandInputModel.
     * @param input A String corresponding to the new text data in the CommandInputModel.
     */
    void setInput(String input);

    /**
     * Clears the data saved in the CommandInputModel.
     */
    void clearInput();
}
