package com.notably.model.suggestion;

import java.util.Optional;

/**
 * Represents the instance of the suggested item.
 */
public interface SuggestionItem {
    /**
     * Gets the property value of the SuggestionItem based on the key.
     *
     * @param key The key to a key-value mapping.
     * @return The value of the mapping.
     */
    Optional<String> getProperty(String key);

    /**
     * Executes the action taken after the suggestion item is chosen.
     * The action will be different depending on the type of SuggestionGenerator.
     *
     * @return The action taken after the suggestion item is chosen.
     */
    Runnable getAction();
}
