package com.notably.logic.suggestion.handler;

import static com.notably.commons.parser.CliSyntax.PREFIX_TITLE;

import java.util.Optional;

import com.notably.commons.parser.ArgumentMultimap;
import com.notably.commons.parser.ArgumentTokenizer;
import com.notably.commons.parser.ParserUtil;
import com.notably.commons.path.AbsolutePath;
import com.notably.logic.correction.CorrectionEngine;
import com.notably.logic.correction.CorrectionResult;
import com.notably.logic.correction.CorrectionStatus;
import com.notably.logic.parser.exceptions.ParseException;
import com.notably.logic.suggestion.generator.OpenSuggestionGenerator;
import com.notably.model.Model;

/**
 * Represents a Handler for OpenSuggestionGenerator.
 */
public class OpenSuggestionArgHandler implements SuggestionArgHandler<OpenSuggestionGenerator> {
    public static final String COMMAND_WORD = "open";

    private static final String RESPONSE_MESSAGE = "Open a note";
    private static final String RESPONSE_MESSAGE_WITH_TITLE = "Open a note titled \"%s\"";
    private static final String ERROR_MESSAGE_CANNOT_OPEN_NOTE = "Cannot open \"%s\" as it is an invalid path";

    private Model model;
    private CorrectionEngine<AbsolutePath> pathCorrectionEngine;

    public OpenSuggestionArgHandler(Model model, CorrectionEngine<AbsolutePath> pathCorrectionEngine) {
        this.model = model;
        this.pathCorrectionEngine = pathCorrectionEngine;
    }

    /**
     * Handles user input in the context of the OpenSuggestionGenerator.
     *
     * @param userInput The user's input.
     * @return An optional OpenSuggestionGenerator object with a corrected absolute path.
     */
    @Override
    public Optional<OpenSuggestionGenerator> handleArg(String userInput) {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(userInput, PREFIX_TITLE);

        String title;
        if (!ParserUtil.arePrefixesPresent(argMultimap, PREFIX_TITLE)
                || !argMultimap.getPreamble().isEmpty()) {
            title = userInput.trim();
        } else {
            title = argMultimap.getValue(PREFIX_TITLE).get();
        }

        if (title.isEmpty()) {
            model.setResponseText(RESPONSE_MESSAGE);
            return Optional.empty();
        }

        AbsolutePath uncorrectedPath;
        try {
            uncorrectedPath = ParserUtil.createAbsolutePath(title, model.getCurrentlyOpenPath());
        } catch (ParseException pe) {
            model.setResponseText(String.format(ERROR_MESSAGE_CANNOT_OPEN_NOTE, title));
            return Optional.empty();
        }

        model.setResponseText(String.format(RESPONSE_MESSAGE_WITH_TITLE, title));

        CorrectionResult<AbsolutePath> correctionResult = pathCorrectionEngine.correct(uncorrectedPath);
        if (correctionResult.getCorrectionStatus() == CorrectionStatus.FAILED) {
            return Optional.empty();
        }

        return Optional.of(new OpenSuggestionGenerator(correctionResult.getCorrectedItems(), title));

    }
}
