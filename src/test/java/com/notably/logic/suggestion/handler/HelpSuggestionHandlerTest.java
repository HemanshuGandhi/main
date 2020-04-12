package com.notably.logic.suggestion.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.notably.logic.suggestion.SuggestionTestUtil;
import com.notably.logic.suggestion.generator.SuggestionGenerator;
import com.notably.model.Model;

public class HelpSuggestionHandlerTest {
    private static HelpSuggestionHandler helpSuggestionHandler;
    private static Model model;

    private static final String RESPONSE_MESSAGE = "Display a list of available commands";

    @BeforeAll
    public static void setUp() {
        model = SuggestionTestUtil.getModel();

        helpSuggestionHandler = new HelpSuggestionHandler(model);
    }

    @Test
    public void parse() {
        Optional<? extends SuggestionGenerator> command = helpSuggestionHandler.handle();

        assertEquals(Optional.of(RESPONSE_MESSAGE),
                model.responseTextProperty().getValue());
    }
}
