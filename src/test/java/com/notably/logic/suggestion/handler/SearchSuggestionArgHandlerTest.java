package com.notably.logic.suggestion.handler;

import static com.notably.commons.parser.CliSyntax.PREFIX_SEARCH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.notably.logic.suggestion.SuggestionTestUtil;
import com.notably.logic.suggestion.generator.SearchSuggestionGenerator;
import com.notably.model.Model;
import com.notably.model.suggestion.SuggestionItem;

public class SearchSuggestionArgHandlerTest {
    private static SearchSuggestionArgHandler searchSuggestionArgHandler;
    private static Model model;

    private static final String RESPONSE_MESSAGE = "Search through all notes based on keyword";
    private static final String RESPONSE_MESSAGE_WITH_KEYWORD = "Search through all notes based on keyword \"%s\"";

    @BeforeAll
    public static void setUp() {
        model = SuggestionTestUtil.getModel();
        searchSuggestionArgHandler = new SearchSuggestionArgHandler(model);
    }

    @AfterEach
    public void clearResponseTextAndSuggestions() {
        model.clearResponseText();
        model.clearSuggestions();
    }

    @Test
    public void parse_emptyKeyword_returnsOptionalEmpty() {
        String keyword = "";
        String userInput = " " + PREFIX_SEARCH + " " + keyword;
        Optional<? extends SearchSuggestionGenerator> command = searchSuggestionArgHandler.handleArg(userInput);

        assertEquals(Optional.of(RESPONSE_MESSAGE), model.responseTextProperty().getValue());

        assertFalse(command.isPresent());
    }

    @Test
    public void parse_withPrefixSearch_returnsSearchSuggestionCommand() {
        String keyword = "fAlSe";
        String userInput = " " + PREFIX_SEARCH + " " + keyword;
        Optional<? extends SearchSuggestionGenerator> command = searchSuggestionArgHandler.handleArg(userInput);
        assertTrue(command.get() instanceof SearchSuggestionGenerator);

        command.get().execute(model);

        assertEquals(Optional.of(String.format(RESPONSE_MESSAGE_WITH_KEYWORD, keyword)),
                model.responseTextProperty().getValue());

        List<SuggestionItem> expectedSuggestions = SuggestionTestUtil.getExpectedSearchSugForKeywordFalse();
        List<SuggestionItem> suggestions = model.getSuggestions();
        SuggestionTestUtil.assertSearchSuggestions(expectedSuggestions, suggestions, model);
    }

    @Test
    public void parse_withoutPrefixSearch_returnsSearchSuggestionCommand() {
        String keyword = "fAlSe";
        String userInput = keyword;
        Optional<? extends SearchSuggestionGenerator> command = searchSuggestionArgHandler.handleArg(userInput);
        assertTrue(command.get() instanceof SearchSuggestionGenerator);

        command.get().execute(model);

        assertEquals(Optional.of(String.format(RESPONSE_MESSAGE_WITH_KEYWORD, keyword)),
                model.responseTextProperty().getValue());

        List<SuggestionItem> expectedSuggestions = SuggestionTestUtil.getExpectedSearchSugForKeywordFalse();
        List<SuggestionItem> suggestions = model.getSuggestions();
        SuggestionTestUtil.assertSearchSuggestions(expectedSuggestions, suggestions, model);
    }
}
