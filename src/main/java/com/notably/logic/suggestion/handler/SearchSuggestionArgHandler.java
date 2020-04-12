package com.notably.logic.suggestion.handler;

import static com.notably.commons.parser.CliSyntax.PREFIX_SEARCH;

import java.util.Optional;

import com.notably.commons.parser.ArgumentMultimap;
import com.notably.commons.parser.ArgumentTokenizer;
import com.notably.commons.parser.ParserUtil;
import com.notably.logic.suggestion.generator.SearchSuggestionGenerator;
import com.notably.model.Model;

/**
 * Represents a Handler for SearchSuggestionGenerator.
 */
public class SearchSuggestionArgHandler implements SuggestionArgHandler<SearchSuggestionGenerator> {
    public static final String COMMAND_WORD = "search";

    private static final String RESPONSE_MESSAGE = "Search through all notes based on keyword";
    private static final String RESPONSE_MESSAGE_WITH_KEYWORD = "Search through all notes based on keyword \"%s\"";

    private Model model;

    public SearchSuggestionArgHandler(Model model) {
        this.model = model;
    }

    /**
     * Handles user input in the context of the SearchSuggestionGenerator.
     *
     * @param userInput The user's input.
     * @return An optional SearchSuggestionGenerator object
     */
    @Override
    public Optional<SearchSuggestionGenerator> handleArg(String userInput) {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(userInput, PREFIX_SEARCH);

        String keyword;
        if (!ParserUtil.arePrefixesPresent(argMultimap, PREFIX_SEARCH)
                || !argMultimap.getPreamble().isEmpty()) {
            keyword = userInput.trim();
        } else {
            keyword = argMultimap.getValue(PREFIX_SEARCH).get();
        }

        if (keyword.isEmpty()) {
            model.setResponseText(RESPONSE_MESSAGE);
            return Optional.empty();
        }

        model.setResponseText(String.format(RESPONSE_MESSAGE_WITH_KEYWORD, keyword));
        return Optional.of(new SearchSuggestionGenerator(keyword));
    }
}
