package com.notably.logic.suggestion.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.notably.logic.suggestion.SuggestionTestUtil;
import com.notably.logic.suggestion.generator.SuggestionGenerator;
import com.notably.model.Model;

public class ExitSuggestionHandlerTest {
    private static ExitSuggestionHandler exitSuggestionHandler;
    private static Model model;

    private static final String RESPONSE_MESSAGE = "Exit the application";

    @BeforeAll
    public static void setUp() {
        model = SuggestionTestUtil.getModel();

        exitSuggestionHandler = new ExitSuggestionHandler(model);
    }

    @Test
    public void parse() {
        Optional<? extends SuggestionGenerator> command = exitSuggestionHandler.handle();

        assertEquals(Optional.of(RESPONSE_MESSAGE),
                model.responseTextProperty().getValue());
    }
}
