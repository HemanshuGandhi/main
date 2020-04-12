package com.notably.model.userpref;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import com.notably.commons.GuiSettings;

/**
 * Represents User's preferences.
 */
public class UserPrefModelImpl implements UserPrefModel {
    private GuiSettings guiSettings = new GuiSettings();
    private Path blockDataFilePath = Paths.get("data" , "blockdata.json");

    /**
     * Creates a {@code UserPrefs} with default values.
     */
    public UserPrefModelImpl() {}

    /**
     * Creates a {@code UserPrefs} with the prefs in {@code userPrefs}.
     *
     * @param userPrefs {@code UserPrefs} data
     */
    public UserPrefModelImpl(ReadOnlyUserPrefModel userPrefs) {
        this();
        resetUserPrefModel(userPrefs);
    }

    @Override
    public void setUserPrefModel(ReadOnlyUserPrefModel userPrefModel) {
        resetUserPrefModel(userPrefModel);
    }

    @Override
    public ReadOnlyUserPrefModel getUserPrefModel() {
        return this;
    }

    @Override
    public void resetUserPrefModel(ReadOnlyUserPrefModel newUserPrefModel) {
        requireNonNull(newUserPrefModel);
        setGuiSettings(newUserPrefModel.getGuiSettings());
        setBlockDataFilePath(newUserPrefModel.getBlockDataFilePath());
    }

    @Override
    public GuiSettings getGuiSettings() {
        return guiSettings;
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        this.guiSettings = guiSettings;
    }

    @Override
    public Path getBlockDataFilePath() {
        return blockDataFilePath;
    }

    @Override
    public void setBlockDataFilePath(Path blockDataFilePath) {
        requireNonNull(blockDataFilePath);
        this.blockDataFilePath = blockDataFilePath;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UserPrefModel)) { //this handles null as well.
            return false;
        }

        UserPrefModel o = (UserPrefModel) other;

        return guiSettings.equals(o.getGuiSettings())
                && blockDataFilePath.equals(o.getBlockDataFilePath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(guiSettings, blockDataFilePath);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Gui Settings : " + guiSettings);
        sb.append("\nLocal data file location : " + blockDataFilePath);
        return sb.toString();
    }

}
