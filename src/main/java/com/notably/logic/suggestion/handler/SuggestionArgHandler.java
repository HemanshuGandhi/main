package com.notably.logic.suggestion.handler;

import java.util.Optional;

import com.notably.logic.suggestion.generator.SuggestionGenerator;

/**
 * Represents a Suggestion Argument Handler that is able to handle user input into a SuggestionGenerator.
 *
 * @param <T> Generic type of SuggestionGenerator.
 */
public interface SuggestionArgHandler<T extends SuggestionGenerator> {
    /**
     * Handles the arguments part of the user input into a suggestion generator and returns it.
     *
     * @param userInput The user input.
     * @return The optional suggestion generator.
     */
    Optional<T> handleArg(String userInput);
}
