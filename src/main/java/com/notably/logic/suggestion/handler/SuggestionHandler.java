package com.notably.logic.suggestion.handler;

import java.util.Optional;

import com.notably.logic.suggestion.generator.SuggestionGenerator;

/**
 * Represents a Suggestion Handler.
 *
 * @param <T> Generic type of SuggestionGenerator.
 */
public interface SuggestionHandler<T extends SuggestionGenerator> {
    /**
     * Handles suggestions.
     *
     * @return The optional suggestion generator.
     */
    Optional<T> handle();
}
