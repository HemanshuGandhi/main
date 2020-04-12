package com.notably.logic.suggestion.generator;

import com.notably.model.Model;

/**
 * Represents a particular strategy in generating suggestions.
 */
public interface SuggestionGenerator {
    /**
     * Adds a list of generated suggestions to the app's model.
     *
     * @param model The app's model.
     */
    void execute(Model model);
}
