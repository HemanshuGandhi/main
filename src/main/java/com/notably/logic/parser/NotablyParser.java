package com.notably.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.notably.commons.LogsCenter;
import com.notably.commons.path.AbsolutePath;
import com.notably.logic.commands.Command;
import com.notably.logic.commands.DeleteCommand;
import com.notably.logic.commands.EditCommand;
import com.notably.logic.commands.ExitCommand;
import com.notably.logic.commands.HelpCommand;
import com.notably.logic.commands.NewCommand;
import com.notably.logic.commands.OpenCommand;
import com.notably.logic.correction.AbsolutePathCorrectionEngine;
import com.notably.logic.correction.CorrectionEngine;
import com.notably.logic.correction.CorrectionEngineParameters;
import com.notably.logic.correction.CorrectionResult;
import com.notably.logic.correction.CorrectionStatus;
import com.notably.logic.correction.StringCorrectionEngine;
import com.notably.logic.parser.exceptions.ParseException;
import com.notably.model.Model;

/**
 * Parse in users input and generate the respective Commands
 */
public class NotablyParser {
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    private static final List<String> COMMAND_LIST = List.of(
            NewCommand.COMMAND_WORD, EditCommand.COMMAND_WORD,
            DeleteCommand.COMMAND_WORD, OpenCommand.COMMAND_WORD,
            HelpCommand.COMMAND_WORD, ExitCommand.COMMAND_WORD,
            SearchCommandParser.COMMAND_WORD);

    private static final String ERROR_MESSAGE = "\"%s\" is an invalid command format. "
            + "To see the list of available commands, type: help";
    private final Logger logger = LogsCenter.getLogger(NotablyParser.class);

    private Model notablyModel;
    private final CorrectionEngine<String> commandCorrectionEngine;
    private final CorrectionEngine<AbsolutePath> pathCorrectionEngine;

    public NotablyParser(Model notablyModel) {
        Objects.requireNonNull(notablyModel);

        this.notablyModel = notablyModel;
        this.commandCorrectionEngine = new StringCorrectionEngine(COMMAND_LIST,
                new CorrectionEngineParameters().setForwardMatching(true));
        this.pathCorrectionEngine = new AbsolutePathCorrectionEngine(notablyModel,
                new CorrectionEngineParameters().setForwardMatching(false));
    }
    /**
     * Create list of different Commands base on user input.
     * @param userInput supplied by the user.
     * @return list of commands.
     * @throws ParseException when there is a invalid input string.
     */
    public List<? extends Command> parseCommand(String userInput) throws ParseException {
        requireNonNull(userInput);
        logger.info(String.format("Parsing '%s'", userInput));
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            logger.warning(String.format("'%s' is an invalid command format.", userInput));
            throw new ParseException(String.format(ERROR_MESSAGE, userInput));
        }

        String commandWord = matcher.group("commandWord");
        CorrectionResult<String> correctionResult = commandCorrectionEngine.correct(commandWord);
        if (correctionResult.getCorrectionStatus() == CorrectionStatus.FAILED) {
            logger.warning(String.format("'%s' is an invalid command word.", commandWord));
            throw new ParseException(String.format(ERROR_MESSAGE, commandWord));
        }

        commandWord = correctionResult.getCorrectedItems().get(0);

        final String arguments = matcher.group("arguments");

        switch (commandWord) {
        case NewCommand.COMMAND_WORD:
            return new NewCommandParser(notablyModel).parse(arguments);

        case OpenCommand.COMMAND_WORD:
            return new OpenCommandParser(notablyModel, pathCorrectionEngine).parse(arguments);

        case DeleteCommand.COMMAND_WORD:
            return new DeleteCommandParser(notablyModel, pathCorrectionEngine).parse(arguments);

        case EditCommand.COMMAND_WORD:
            return List.of(new EditCommand());

        case HelpCommand.COMMAND_WORD:
            return List.of(new HelpCommand());

        case ExitCommand.COMMAND_WORD:
            return List.of(new ExitCommand());
        case SearchCommandParser.COMMAND_WORD:
            new SearchCommandParser(notablyModel).parse(arguments);
            return List.of();
        default:
            throw new ParseException(String.format(ERROR_MESSAGE, commandWord));
        }
    }

}

