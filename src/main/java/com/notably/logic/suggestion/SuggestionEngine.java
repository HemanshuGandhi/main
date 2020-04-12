package com.notably.logic.suggestion;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.notably.commons.path.AbsolutePath;
import com.notably.logic.commands.suggestion.SuggestionCommand;
import com.notably.logic.correction.AbsolutePathCorrectionEngine;
import com.notably.logic.correction.CorrectionEngine;
import com.notably.logic.correction.CorrectionEngineParameters;
import com.notably.logic.correction.CorrectionResult;
import com.notably.logic.correction.CorrectionStatus;
import com.notably.logic.correction.StringCorrectionEngine;
import com.notably.logic.parser.suggestion.DeleteSuggestionCommandParser;
import com.notably.logic.parser.suggestion.EditSuggestionCommandParser;
import com.notably.logic.parser.suggestion.ErrorSuggestionCommandParser;
import com.notably.logic.parser.suggestion.ExitSuggestionCommandParser;
import com.notably.logic.parser.suggestion.HelpSuggestionCommandParser;
import com.notably.logic.parser.suggestion.NewSuggestionCommandParser;
import com.notably.logic.parser.suggestion.OpenSuggestionCommandParser;
import com.notably.logic.parser.suggestion.SearchSuggestionCommandParser;
import com.notably.model.Model;

import javafx.beans.property.StringProperty;

/**
 * Represents a SuggestionEngine.
 */
public class SuggestionEngine {
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    private static final List<String> COMMAND_LIST = List.of(NewSuggestionCommandParser.COMMAND_WORD,
            EditSuggestionCommandParser.COMMAND_WORD, DeleteSuggestionCommandParser.COMMAND_WORD,
            OpenSuggestionCommandParser.COMMAND_WORD, HelpSuggestionCommandParser.COMMAND_WORD,
            ExitSuggestionCommandParser.COMMAND_WORD, SearchSuggestionCommandParser.COMMAND_WORD);

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

        Optional<? extends SuggestionCommand> suggestionCommand = parseCommand(userInput);
        suggestionCommand.ifPresent(s -> s.execute(model));
    }

    /**
     * Parses the user input.
     * @param userInput The user's input.
     * @return The corresponding SuggestionCommand.
     */
    private Optional<? extends SuggestionCommand> parseCommand(String userInput) {
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
        case OpenSuggestionCommandParser.COMMAND_WORD:
            return new OpenSuggestionCommandParser(model, pathCorrectionEngine).parse(arguments);

        case DeleteSuggestionCommandParser.COMMAND_WORD:
            return new DeleteSuggestionCommandParser(model, pathCorrectionEngine).parse(arguments);

        case SearchSuggestionCommandParser.COMMAND_WORD:
            return new SearchSuggestionCommandParser(model).parse(arguments);

        case NewSuggestionCommandParser.COMMAND_WORD:
            return new NewSuggestionCommandParser(model).parse(arguments);

        case EditSuggestionCommandParser.COMMAND_WORD:
            return new EditSuggestionCommandParser(model).parse(arguments);

        case HelpSuggestionCommandParser.COMMAND_WORD:
            return new HelpSuggestionCommandParser(model).parse(arguments);

        case ExitSuggestionCommandParser.COMMAND_WORD:
            return new ExitSuggestionCommandParser(model).parse(arguments);

        default:
            return new ErrorSuggestionCommandParser(model).parse(arguments);
        }
    }

    /**
     * Generates new suggestions whenever the command input line changes.
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
