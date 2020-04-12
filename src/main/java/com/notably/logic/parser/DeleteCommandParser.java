package com.notably.logic.parser;

import static com.notably.logic.parser.CliSyntax.PREFIX_TITLE;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.logging.Logger;

import com.notably.commons.LogsCenter;
import com.notably.commons.path.AbsolutePath;
import com.notably.logic.commands.DeleteCommand;
import com.notably.logic.correction.CorrectionEngine;
import com.notably.logic.correction.CorrectionResult;
import com.notably.logic.correction.CorrectionStatus;
import com.notably.logic.parser.exceptions.ParseException;
import com.notably.model.Model;

/**
 * Parses input arguments and creates a new DeleteCommand object
 */
public class DeleteCommandParser implements CommandParser<DeleteCommand> {
    private static final String ERROR_EMPTY_PATH = "An empty path is detected please enter a valid Path. "
            + "To see list of Command format, type: help";
    private static final String ERROR_NO_MATCH_PATH = "The path \"%s\" does not exist in the Storage";
    private final Logger logger = LogsCenter.getLogger(DeleteCommandParser.class);

    private Model notablyModel;
    private CorrectionEngine<AbsolutePath> pathCorrectionEngine;

    public DeleteCommandParser(Model notablyModel, CorrectionEngine<AbsolutePath> pathCorrectionEngine) {
        this.notablyModel = notablyModel;
        this.pathCorrectionEngine = pathCorrectionEngine;
    }
    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns a DeleteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public List<DeleteCommand> parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_TITLE);

        String title;
        if (!ParserUtil.arePrefixesPresent(argMultimap, PREFIX_TITLE)
                || !argMultimap.getPreamble().isEmpty()) {
            title = args.trim();
        } else {
            title = argMultimap.getValue(PREFIX_TITLE).get();
        }

        if (title.isEmpty()) {
            logger.warning("Empty path detected.");
            throw new ParseException(ERROR_EMPTY_PATH);
        }

        AbsolutePath uncorrectedPath = ParserUtil.createAbsolutePath(title, notablyModel.getCurrentlyOpenPath());
        CorrectionResult<AbsolutePath> correctionResult = pathCorrectionEngine.correct(uncorrectedPath);
        if (correctionResult.getCorrectionStatus() == CorrectionStatus.FAILED) {
            logger.warning(String.format("The path \"%s\" does not exist in the storage.", title));
            throw new ParseException(String.format(ERROR_NO_MATCH_PATH, title));
        }

        AbsolutePath correctedPath = correctionResult.getCorrectedItems().get(0);
        logger.info(String.format("DeleteCommand with the path '%s' is created", correctedPath));
        return List.of(new DeleteCommand(correctedPath));
    }
}
