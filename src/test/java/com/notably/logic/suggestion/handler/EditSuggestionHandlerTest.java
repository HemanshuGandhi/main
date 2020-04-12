package com.notably.logic.suggestion.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.notably.logic.suggestion.SuggestionTestUtil;
import com.notably.logic.suggestion.generator.SuggestionGenerator;
import com.notably.model.Model;

public class EditSuggestionHandlerTest {
    private static EditSuggestionHandler editSuggestionHandler;
    private static Model model;

    private static final String RESPONSE_MESSAGE = "Edit this note";

    @BeforeAll
    public static void setUp() {
        model = SuggestionTestUtil.getModel();

        editSuggestionHandler = new EditSuggestionHandler(model);
    }

    @Test
    public void parse() {
        Optional<? extends SuggestionGenerator> command = editSuggestionHandler.handle();

        assertEquals(Optional.of(RESPONSE_MESSAGE),
                model.responseTextProperty().getValue());
    }
}
