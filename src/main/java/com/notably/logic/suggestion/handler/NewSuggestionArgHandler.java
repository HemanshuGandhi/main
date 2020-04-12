package com.notably.logic.suggestion.handler;

import static com.notably.commons.parser.CliSyntax.PREFIX_JUMP;
import static com.notably.commons.parser.CliSyntax.PREFIX_TITLE;

import java.util.Optional;

import com.notably.commons.parser.ArgumentMultimap;
import com.notably.commons.parser.ArgumentTokenizer;
import com.notably.commons.parser.ParserUtil;
import com.notably.logic.suggestion.generator.SuggestionGenerator;
import com.notably.model.Model;
import com.notably.model.block.Title;

/**
 * Represents a Handler for the command word "new".
 */
public class NewSuggestionArgHandler implements SuggestionArgHandler<SuggestionGenerator> {
    public static final String COMMAND_WORD = "new";

    private static final String RESPONSE_MESSAGE = "Create a new note";
    private static final String RESPONSE_MESSAGE_WITH_TITLE = "Create a new note titled \"%s\".";
    private static final String RESPONSE_MESSAGE_WITH_TITLE_AND_OPEN = "Create a new note titled \"%s\" and open it.";
    private static final String ERROR_MESSAGE_INVALID_COMMAND = "\"%s\" is an invalid creation format. "
            + "The correct format is \"new -t TITLE [-o]\"";
    private static final String ERROR_MESSAGE_INVALID_TITLE = "Title \"%s\" is invalid. "
            + "Titles cannot start with the period character. "
            + "Titles should only contain alphanumeric characters and symbols except - and /";

    private Model model;

    public NewSuggestionArgHandler(Model model) {
        this.model = model;
    }

    /**
     * Handles input and displays the appropriate response text.
     *
     * @param userInput .
     * @return Optional.empty()
     */
    public Optional<SuggestionGenerator> handleArg(String userInput) {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(userInput, PREFIX_TITLE, PREFIX_JUMP);

        if (userInput.isBlank()) {
            model.setResponseText(RESPONSE_MESSAGE);
            return Optional.empty();
        }

        if (!ParserUtil.arePrefixesPresent(argMultimap, PREFIX_TITLE)
                || !argMultimap.getPreamble().isEmpty()) {
            model.setResponseText(String.format(ERROR_MESSAGE_INVALID_COMMAND, model.getInput()));
            return Optional.empty();
        }

        String title = argMultimap.getValue(PREFIX_TITLE).get();

        if (!Title.isValidTitle(title) && !title.isBlank()) {
            model.setResponseText(String.format(ERROR_MESSAGE_INVALID_TITLE, title));
        } else if (title.isBlank()) {
            model.setResponseText(RESPONSE_MESSAGE);
        } else if (!ParserUtil.arePrefixesPresent(argMultimap, PREFIX_JUMP)) { // If user does NOT type "-o"
            model.setResponseText(String.format(RESPONSE_MESSAGE_WITH_TITLE, title));
        } else {
            model.setResponseText(String.format(RESPONSE_MESSAGE_WITH_TITLE_AND_OPEN, title));
        }

        return Optional.empty();

    }
}
