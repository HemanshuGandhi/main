package com.notably.model.viewstate;

import static com.notably.commons.util.CollectionUtil.requireAllNonNull;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents the data that the user can manipulate through interaction with front-facing components
 * such as buttons and text fields.
 */
public class ViewStateModelImpl implements ViewStateModel {

    private final StringProperty input;
    private final BooleanProperty helpOpen;
    private final BooleanProperty blockEditable;
    private final Property<Optional<String>> responseText;

    public ViewStateModelImpl() {
        this("", false, false, Optional.empty());
    }

    private ViewStateModelImpl(String initialInput, boolean initialHelpBool,
                               boolean initialBlockEditBool, Optional<String> initialResponseText) {
        requireAllNonNull(initialInput, initialHelpBool, initialBlockEditBool, initialResponseText);

        this.input = new SimpleStringProperty(initialInput);
        this.helpOpen = new SimpleBooleanProperty(initialHelpBool);
        this.blockEditable = new SimpleBooleanProperty(initialBlockEditBool);
        this.responseText = new SimpleObjectProperty<>(initialResponseText);
    }

    //=========== CommandInputModel ===============================================================
    @Override
    public StringProperty inputProperty() {
        return this.input;
    }

    @Override
    public String getInput() {
        return this.input.getValue();
    }

    @Override
    public void setInput(String input) {
        requireNonNull(input);
        this.input.setValue(input);
    }

    @Override
    public void clearInput() {
        input.setValue("");
    }

    //=========== HelpFlagModel ===================================================================
    @Override
    public BooleanProperty helpOpenProperty() {
        return this.helpOpen;
    }

    @Override
    public Boolean isHelpOpen() {
        return this.helpOpen.getValue();
    }

    @Override
    public void setHelpOpen(Boolean bool) {
        requireNonNull(bool);
        this.helpOpen.setValue(bool);
    }

    //============ BlockEditFlagModel =============================================================
    @Override
    public BooleanProperty blockEditableProperty() {
        return this.blockEditable;
    }

    @Override
    public Boolean isBlockEditable() {
        return this.blockEditable.getValue();
    }

    @Override
    public void setBlockEditable(Boolean bool) {
        requireNonNull(bool);
        this.blockEditable.setValue(bool);
    }

    //============ Response Text ==================================================================
    @Override
    public Property<Optional<String>> responseTextProperty() {
        return responseText;
    }

    @Override
    public void setResponseText(String responseTextStr) {
        requireNonNull(responseTextStr);
        responseText.setValue(Optional.of(responseTextStr));
    }

    @Override
    public void clearResponseText() {
        responseText.setValue(Optional.empty());
    }
}
