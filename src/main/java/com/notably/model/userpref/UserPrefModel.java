package com.notably.model.userpref;

import java.nio.file.Path;

import com.notably.commons.GuiSettings;

/**
 * API of modifiable user's preferences.
 */
public interface UserPrefModel extends ReadOnlyUserPrefModel {
    /**
     * Sets the current user prefs.
     *
     * @param userPrefModel Target {@link ReadOnlyUserPrefModel}
     */
    public void setUserPrefModel(ReadOnlyUserPrefModel userPrefModel);

    /**
     * Gets the current user prefs.
     *
     * @return User prefs currently in use
     */
    public ReadOnlyUserPrefModel getUserPrefModel();

    /**
     * Resets the existing data of this {@link UserPrefModel} with {@code newUserPrefModel}.
     *
     * @param newUserPrefModel Target new user prefs
     */
    public void resetUserPrefModel(ReadOnlyUserPrefModel newUserPrefModel);

    /**
     * Sets the user prefs' GUI settings.
     *
     * @param guiSettings GUI settings
     */
    public void setGuiSettings(GuiSettings guiSettings);

    /**
     * Sets the user prefs' path of block data file.
     *
     * @param blockDataFilePath File path of the block data
     */
    public void setBlockDataFilePath(Path blockDataFilePath);
}
