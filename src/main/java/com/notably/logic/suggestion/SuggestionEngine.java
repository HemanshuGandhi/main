package com.notably.logic.suggestion;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.notably.commons.path.AbsolutePath;
import com.notably.logic.correction.AbsolutePathCorrectionEngine;
import com.notably.logic.correction.CorrectionEngine;
import com.notably.logic.correction.CorrectionEngineParameters;
import com.notably.logic.correction.CorrectionResult;
import com.notably.logic.correction.CorrectionStatus;
import com.notably.logic.correction.StringCorrectionEngine;
import com.notably.logic.suggestion.generator.SuggestionGenerator;
import com.notably.logic.suggestion.handler.DeleteSuggestionArgHandler;
import com.notably.logic.suggestion.handler.EditSuggestionHandler;
import com.notably.logic.suggestion.handler.ExitSuggestionHandler;
import com.notably.logic.suggestion.handler.HelpSuggestionHandler;
import com.notably.logic.suggestion.handler.NewSuggestionArgHandler;
import com.notably.logic.suggestion.handler.OpenSuggestionArgHandler;
import com.notably.logic.suggestion.handler.SearchSuggestionArgHandler;
import com.notably.model.Model;

import javafx.beans.property.StringProperty;

/**
 * Represents a SuggestionEngine.
 */
public class SuggestionEngine {
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    private static final List<String> COMMAND_LIST = List.of(NewSuggestionArgHandler.COMMAND_WORD,
            EditSuggestionHandler.COMMAND_WORD, DeleteSuggestionArgHandler.COMMAND_WORD,
            OpenSuggestionArgHandler.COMMAND_WORD, HelpSuggestionHandler.COMMAND_WORD,
            ExitSuggestionHandler.COMMAND_WORD, SearchSuggestionArgHandler.COMMAND_WORD);

    private static final String ERROR_MESSAGE_INVALID_COMMAND = "\"%s\" is an invalid command format. "
            + "To see the list of available commands, type: help";

    private Model model;
    private CorrectionEngine<String> commandCorrectionEngine;
    private CorrectionEngine<AbsolutePath> pathCorrectionEngine;

    public SuggestionEngine(Model model) {
        Objects.requireNonNull(model);

        this.model = model;
        commandCorrectionEngine = new StringCorrectionEngine(COMMAND_LIST,
                new CorrectionEngineParameters().setForwardMatching(true));
        pathCorrectionEngine = new AbsolutePathCorrectionEngine(model,
                new CorrectionEngineParameters().setForwardMatching(true));

        autoUpdateInput(model.inputProperty());
    }

    /**
     * Generates suggestions.
     *
     * @param userInput The user's input.
     */
    private void suggest(String userInput) {
        Objects.requireNonNull(userInput);

        Optional<? extends SuggestionGenerator> suggestionCommand = parseCommand(userInput);
        suggestionCommand.ifPresent(s -> s.execute(model));
    }

    /**
     * Parses the user input.
     *
     * @param userInput The user's input.
     * @return The corresponding SuggestionGenerator.
     */
    private Optional<? extends SuggestionGenerator> parseCommand(String userInput) {
        Objects.requireNonNull(userInput);

        if (userInput.isBlank()) {
            model.clearSuggestions();
            model.clearResponseText();
            return Optional.empty();
        }

        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            model.setResponseText(String.format(ERROR_MESSAGE_INVALID_COMMAND, userInput));
            return Optional.empty();
        }

        String commandWord = matcher.group("commandWord");
        CorrectionResult<String> correctionResult = commandCorrectionEngine.correct(commandWord);
        if (correctionResult.getCorrectionStatus() == CorrectionStatus.FAILED) {
            model.setResponseText(String.format(ERROR_MESSAGE_INVALID_COMMAND, userInput));
            return Optional.empty();
        }
        commandWord = correctionResult.getCorrectedItems().get(0);

        final String arguments = matcher.group("arguments");

        switch (commandWord) {
        case OpenSuggestionArgHandler.COMMAND_WORD:
            return new OpenSuggestionArgHandler(model, pathCorrectionEngine).handleArg(arguments);

        case DeleteSuggestionArgHandler.COMMAND_WORD:
            return new DeleteSuggestionArgHandler(model, pathCorrectionEngine).handleArg(arguments);

        case SearchSuggestionArgHandler.COMMAND_WORD:
            return new SearchSuggestionArgHandler(model).handleArg(arguments);

        case NewSuggestionArgHandler.COMMAND_WORD:
            return new NewSuggestionArgHandler(model).handleArg(arguments);

        case EditSuggestionHandler.COMMAND_WORD:
            return new EditSuggestionHandler(model).handle();

        case HelpSuggestionHandler.COMMAND_WORD:
            return new HelpSuggestionHandler(model).handle();

        case ExitSuggestionHandler.COMMAND_WORD:
            return new ExitSuggestionHandler(model).handle();

        default:
            throw new AssertionError("Default path will never get executed.");
        }
    }

    /**
     * Generates new suggestions whenever the command input line changes.
     *
     * @param inputProperty The user's input.
     */
    private void autoUpdateInput(StringProperty inputProperty) {
        Objects.requireNonNull(inputProperty);

        inputProperty.addListener((observable, oldValue, newValue) -> {
            model.clearSuggestions();
            model.clearResponseText();
            suggest(newValue);
        });
    }
}
