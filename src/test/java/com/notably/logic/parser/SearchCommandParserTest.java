package com.notably.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.notably.logic.parser.exceptions.ParseException;
import com.notably.model.Model;
import com.notably.model.ModelManager;
import com.notably.model.block.BlockModel;
import com.notably.model.suggestion.SuggestionItemImpl;
import com.notably.model.suggestion.SuggestionModel;
import com.notably.model.suggestion.SuggestionModelImpl;
import com.notably.model.viewstate.ViewStateModel;
import com.notably.model.viewstate.ViewStateModelImpl;
import com.notably.testutil.TypicalBlockModel;

class SearchCommandParserTest {
    private static Model model;
    private static SearchCommandParser searchCommandParser;

    @BeforeEach
    public void setUp() {
        // Set up model
        BlockModel blockModel = TypicalBlockModel.getTypicalBlockModel();
        SuggestionModel suggestionModel = new SuggestionModelImpl();
        ViewStateModel viewStateModel = new ViewStateModelImpl();
        model = new ModelManager(blockModel, suggestionModel, viewStateModel);
        searchCommandParser = new SearchCommandParser(model);
    }

    @Test
    void parse_nonEmptySuggestionList_throwsParseExceptionEmptySuggestion() {
        Runnable action = () -> {};
        model.setSuggestions(List.of(new SuggestionItemImpl("search /CS2103T", action)));

        final String expectedErrorMessage = "Please select one of the suggestions below, then press enter!";
        try {
            searchCommandParser.parse("CS2103T content");
        } catch (ParseException ex) {
            assertEquals(expectedErrorMessage, ex.getMessage());
        }

    }

    @Test
    void parse_emptySuggestionList_throwsParseExceptionSelectOne() {
        final String arg = "CS2103T content";
        final String expectedErrorMessage = String.format
                ("Unable to search for notes containing the keyword '%s'", arg);

        try {
            searchCommandParser.parse(arg);
        } catch (ParseException ex) {
            assertEquals(expectedErrorMessage, ex.getMessage());
        }
    }
}
