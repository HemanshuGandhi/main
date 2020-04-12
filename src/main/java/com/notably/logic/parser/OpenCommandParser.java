package com.notably.logic.parser;

import static com.notably.commons.parser.CliSyntax.PREFIX_TITLE;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.logging.Logger;

import com.notably.commons.LogsCenter;
import com.notably.commons.parser.ArgumentMultimap;
import com.notably.commons.parser.ArgumentTokenizer;
import com.notably.commons.parser.ParserUtil;
import com.notably.commons.path.AbsolutePath;
import com.notably.logic.commands.OpenCommand;
import com.notably.logic.correction.CorrectionEngine;
import com.notably.logic.correction.CorrectionResult;
import com.notably.logic.correction.CorrectionStatus;
import com.notably.logic.parser.exceptions.ParseException;
import com.notably.model.Model;

/**
 * Represent a Parser for OpenCommand.
 */
public class OpenCommandParser implements CommandParser<OpenCommand> {
    private static final String ERROR_PATH = "The Path \"%s\" does not exist in the storage. "
            + "Please provide a valid Path";
    private static final String ERROR_EMPTY_PATH = "Empty path detected. "
            + "Please provide a valid Path";
    private static final String ERROR_ROOT_PATH = "Opening the root path is forbidden. "
            + "Please provide a valid Path";
    private static final Logger logger = LogsCenter.getLogger(OpenCommandParser.class);
    private static final AbsolutePath ROOT = AbsolutePath.fromString("/");
    private Model notablyModel;
    private CorrectionEngine<AbsolutePath> pathCorrectionEngine;

    public OpenCommandParser(Model notablyModel, CorrectionEngine<AbsolutePath> pathCorrectionEngine) {
        this.notablyModel = notablyModel;
        this.pathCorrectionEngine = pathCorrectionEngine;
    }
    /**
     * Creates OpenCommand with user input.
     * @param args to be parse by into CorrectionEngine.
     * @return List of command containing OpenCammnd.
     * @throws ParseException
     */
    public List<OpenCommand> parse(String args) throws ParseException {
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
            logger.warning("Empty path detected");
            throw new ParseException(ERROR_EMPTY_PATH);
        }

        AbsolutePath uncorrectedPath = ParserUtil.createAbsolutePath(title, notablyModel.getCurrentlyOpenPath());
        CorrectionResult<AbsolutePath> correctionResult = pathCorrectionEngine.correct(uncorrectedPath);
        if (correctionResult.getCorrectionStatus() == CorrectionStatus.FAILED) {
            logger.warning(String.format("The path \"%s\" does not exist in the storage.", title));
            throw new ParseException(String.format(ERROR_PATH, title));
        }

        AbsolutePath correctedPath = correctionResult.getCorrectedItems().get(0);
        if (correctedPath.equals(ROOT)) {
            logger.warning("Root path detected");
            throw new ParseException(ERROR_ROOT_PATH);
        }
        logger.info(String.format("OpenCommand with the path '%s' is created", correctedPath));
        return List.of(new OpenCommand(correctedPath));
    }
}
