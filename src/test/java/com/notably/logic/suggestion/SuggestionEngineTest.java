package com.notably.logic.suggestion;

import static com.notably.commons.parser.CliSyntax.PREFIX_TITLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.notably.commons.path.AbsolutePath;
import com.notably.commons.path.exceptions.InvalidPathException;
import com.notably.model.Model;
import com.notably.model.suggestion.SuggestionItem;

public class SuggestionEngineTest {
    private static AbsolutePath toCs2103t;
    private static List<AbsolutePath> paths;
    private static Model model;

    private static final String DELETE_RESPONSE_MESSAGE_WITH_TITLE = "Delete a note titled \"%s\"";
    private static final String OPEN_RESPONSE_MESSAGE_WITH_TITLE = "Open a note titled \"%s\"";
    private static final String NEW_RESPONSE_MESSAGE_WITH_TITLE = "Create a new note titled \"%s\".";
    private static final String SEARCH_RESPONSE_MESSAGE_WITH_KEYWORD = "Search through all notes based "
            + "on keyword \"%s\"";
    private static final String EDIT_RESPONSE_MESSAGE = "Edit this note";
    private static final String ERROR_RESPONSE_MESSAGE = "\"%s\" is an invalid command format. "
            + "To see the list of available commands, type: help";
    private static final String EXIT_RESPONSE_MESSAGE = "Exit the application";
    private static final String HELP_RESPONSE_MESSAGE = "Display a list of available commands";

    @BeforeAll
    public static void setUp() throws InvalidPathException {
        model = SuggestionTestUtil.getModel();
        toCs2103t = SuggestionTestUtil.getToCs2103t();
        paths = new ArrayList<>();
        paths.add(toCs2103t);
    }

    @AfterEach
    public void clearResponseTextAndSuggestions() {
        model.clearResponseText();
        model.clearSuggestions();
    }

    @Test
    public void suggest_correctedDeleteCommand_generatesSuggestions() {
        String userInputWithoutPath = "dele ";
        String path = toCs2103t.getStringRepresentation();
        String userInput = userInputWithoutPath + path;
        SuggestionEngine suggestionEngine = new SuggestionEngine(model);
        model.setInput(userInput);

        List<SuggestionItem> suggestions = model.getSuggestions();

        List<SuggestionItem> expectedSuggestions = SuggestionTestUtil.getExpectedSugForCs2103tPathInput();
        SuggestionTestUtil.assertSuggestions(expectedSuggestions, suggestions);

        assertEquals(Optional.of(String.format(DELETE_RESPONSE_MESSAGE_WITH_TITLE, path)),
                model.responseTextProperty().getValue());
    }

    @Test
    public void suggest_correctedOpenCommand_generatesSuggestions() {
        String userInputWithoutPath = "op ";
        String path = toCs2103t.getStringRepresentation();
        String userInput = userInputWithoutPath + path;
        SuggestionEngine suggestionEngine = new SuggestionEngine(model);
        model.setInput(userInput);

        List<SuggestionItem> suggestions = model.getSuggestions();

        List<SuggestionItem> expectedSuggestions = SuggestionTestUtil.getExpectedSugForCs2103tPathInput();
        SuggestionTestUtil.assertSuggestions(expectedSuggestions, suggestions);

        assertEquals(Optional.of(String.format(OPEN_RESPONSE_MESSAGE_WITH_TITLE, path)),
                model.responseTextProperty().getValue());
    }

    @Test
    public void suggest_correctedNewCommand() {
        String userInputWithoutPath = "nw ";
        String path = "Aa123!@#$%^&*()";
        String arg = " " + PREFIX_TITLE + " " + path;
        String userInput = userInputWithoutPath + arg;
        SuggestionEngine suggestionEngine = new SuggestionEngine(model);
        model.setInput(userInput);

        assertEquals(Optional.of(String.format(NEW_RESPONSE_MESSAGE_WITH_TITLE, path)),
                model.responseTextProperty().getValue());
    }

    @Test
    public void suggest_correctedSearchCommand() {
        String userInputWithoutKeyword = "sear ";
        String keyword = "FAlse";
        String userInput = userInputWithoutKeyword + " " + keyword;
        SuggestionEngine suggestionEngine = new SuggestionEngine(model);
        model.setInput(userInput);

        List<SuggestionItem> suggestions = model.getSuggestions();

        assertEquals(Optional.of(String.format(SEARCH_RESPONSE_MESSAGE_WITH_KEYWORD, keyword)),
                model.responseTextProperty().getValue());

        List<SuggestionItem> expectedSuggestions = SuggestionTestUtil.getExpectedSearchSugForKeywordFalse();
        SuggestionTestUtil.assertSearchSuggestions(expectedSuggestions, suggestions, model);

    }

    @Test
    public void suggest_correctedEditCommand() {
        String userInput = "edt";
        SuggestionEngine suggestionEngine = new SuggestionEngine(model);
        model.setInput(userInput);

        assertEquals(Optional.of(EDIT_RESPONSE_MESSAGE), model.responseTextProperty().getValue());
    }

    @Test
    public void suggest_correctedHelpCommand() {
        String userInput = "hAlp";
        SuggestionEngine suggestionEngine = new SuggestionEngine(model);
        model.setInput(userInput);

        assertEquals(Optional.of(HELP_RESPONSE_MESSAGE), model.responseTextProperty().getValue());
    }

    @Test
    public void suggest_correctedExitCommand() {
        String userInput = "ex";
        SuggestionEngine suggestionEngine = new SuggestionEngine(model);
        model.setInput(userInput);

        assertEquals(Optional.of(EXIT_RESPONSE_MESSAGE), model.responseTextProperty().getValue());
    }

    @Test
    public void suggest_invalidCommand_displaysErrorMessage() {
        String userInput = "randomCmd";
        SuggestionEngine suggestionEngine = new SuggestionEngine(model);
        model.setInput(userInput);

        assertEquals(Optional.of(String.format(ERROR_RESPONSE_MESSAGE, userInput)),
                model.responseTextProperty().getValue());
    }
}
