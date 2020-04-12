package com.notably.model;

import static com.notably.commons.util.CollectionUtil.requireAllNonNull;
import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.notably.commons.GuiSettings;
import com.notably.commons.LogsCenter;
import com.notably.commons.path.AbsolutePath;
import com.notably.model.block.Block;
import com.notably.model.block.BlockModel;
import com.notably.model.block.BlockTree;
import com.notably.model.block.Body;
import com.notably.model.suggestion.SuggestionItem;
import com.notably.model.suggestion.SuggestionModel;
import com.notably.model.userpref.ReadOnlyUserPrefModel;
import com.notably.model.userpref.UserPrefModel;
import com.notably.model.userpref.UserPrefModelImpl;
import com.notably.model.viewstate.ViewStateModel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

/**
 * Represents the in-memory model of the application data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private UserPrefModel userPrefModel;
    private SuggestionModel suggestionModel;
    private ViewStateModel viewStateModel;
    private BlockModel blockModel;

    /**
     * Initializes a ModelManager with the given model parts.
     */
    public ModelManager(BlockModel blockModel, SuggestionModel suggestionModel,
                        ViewStateModel viewStateModel, ReadOnlyUserPrefModel userPrefModel) {
        requireAllNonNull(blockModel, suggestionModel, viewStateModel, userPrefModel);

        logger.fine("Initializing with block data: " + blockModel + " and user prefs " + userPrefModel);

        this.suggestionModel = suggestionModel;
        this.viewStateModel = viewStateModel;
        this.blockModel = blockModel;
        this.userPrefModel = new UserPrefModelImpl(userPrefModel);
    }

    /**
     * Shorthand for initializing a {@link ModelManager} without {@link UserPrefModel}.
     */
    public ModelManager(BlockModel blockModel, SuggestionModel suggestionModel, ViewStateModel viewStateModel) {
        this(blockModel, suggestionModel, viewStateModel, new UserPrefModelImpl());
    }

    //=========== UserPrefModel ==================================================================================

    @Override
    public void setUserPrefModel(ReadOnlyUserPrefModel userPrefModel) {
        requireNonNull(userPrefModel);
        this.userPrefModel.setUserPrefModel(userPrefModel);
    }

    @Override
    public ReadOnlyUserPrefModel getUserPrefModel() {
        return userPrefModel.getUserPrefModel();
    }

    @Override
    public void resetUserPrefModel(ReadOnlyUserPrefModel newUserPrefModel) {
        requireNonNull(newUserPrefModel);
        userPrefModel.resetUserPrefModel(newUserPrefModel);
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefModel.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefModel.setGuiSettings(guiSettings);
    }

    @Override
    public Path getBlockDataFilePath() {
        return userPrefModel.getBlockDataFilePath();
    }

    @Override
    public void setBlockDataFilePath(Path blockDataFilePath) {
        requireNonNull(blockDataFilePath);
        userPrefModel.setBlockDataFilePath(blockDataFilePath);
    }

    //=========== Suggestion Model =============================================================

    @Override
    public ObservableList<SuggestionItem> getSuggestions() {
        return suggestionModel.getSuggestions();
    }

    @Override
    public void setSuggestions(List<SuggestionItem> suggestions) {
        suggestionModel.setSuggestions(suggestions);
    }

    @Override
    public Property<Optional<String>> responseTextProperty() {
        return viewStateModel.responseTextProperty();
    }

    @Override
    public void setResponseText(String responseText) {
        viewStateModel.setResponseText(responseText);
    }

    @Override
    public void clearResponseText() {
        viewStateModel.clearResponseText();
    }

    @Override
    public void clearSuggestions() {
        suggestionModel.clearSuggestions();
    }

    //=========== ViewStateModel ==================================================================

    @Override
    public StringProperty inputProperty() {
        return viewStateModel.inputProperty();
    }

    @Override
    public String getInput() {
        return viewStateModel.getInput();
    }

    @Override
    public void setInput(String input) {
        viewStateModel.setInput(input);
    }

    @Override
    public void clearInput() {
        viewStateModel.clearInput();
    }

    @Override
    public BooleanProperty helpOpenProperty() {
        return viewStateModel.helpOpenProperty();
    }

    @Override
    public Boolean isHelpOpen() {
        return viewStateModel.isHelpOpen();
    }

    @Override
    public void setHelpOpen(Boolean bool) {
        viewStateModel.setHelpOpen(bool);
    }

    @Override
    public BooleanProperty blockEditableProperty() {
        return viewStateModel.blockEditableProperty();
    }

    @Override
    public Boolean isBlockEditable() {
        return viewStateModel.isBlockEditable();
    }

    @Override
    public void setBlockEditable(Boolean bool) {
        viewStateModel.setBlockEditable(bool);
    }

    //========= BlockModel========================================================================

    @Override
    public BlockTree getBlockTree() {
        return blockModel.getBlockTree();
    }

    @Override
    public void setBlockTree(BlockTree blockTree) {
        blockModel.setBlockTree(blockTree);
    }

    @Override
    public void resetData(BlockModel newData) {
        blockModel.resetData(newData);
    }

    @Override
    public AbsolutePath getCurrentlyOpenPath() {
        return blockModel.getCurrentlyOpenPath();
    }

    @Override
    public Property<AbsolutePath> currentlyOpenPathProperty() {
        return blockModel.currentlyOpenPathProperty();
    }

    @Override
    public boolean hasPath(AbsolutePath p) {
        return blockModel.hasPath(p);
    }

    @Override
    public void setCurrentlyOpenBlock(AbsolutePath p) {
        blockModel.setCurrentlyOpenBlock(p);
    }

    @Override
    public void addBlockToCurrentPath(Block b) {
        blockModel.addBlockToCurrentPath(b);
    }

    @Override
    public void removeBlock(AbsolutePath p) {
        blockModel.removeBlock(p);
    }

    @Override
    public void updateCurrentlyOpenBlockBody(Body newBody) {
        blockModel.updateCurrentlyOpenBlockBody(newBody);
    }
}
