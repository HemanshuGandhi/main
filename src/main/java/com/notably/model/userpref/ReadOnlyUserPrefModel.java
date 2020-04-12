package com.notably.model.userpref;

import java.nio.file.Path;

import com.notably.commons.GuiSettings;

/**
 * API of unmodifiable user's preferences.
 */
public interface ReadOnlyUserPrefModel {
    /**
     * Returns the user prefs' GUI settings.
     *
     * @return {@link GuiSettings}
     */
    GuiSettings getGuiSettings();

    /**
     * Gets the {@link Path} of the block data file.
     *
     * @return File {@link Path} of the block data file
     */
    Path getBlockDataFilePath();
}
