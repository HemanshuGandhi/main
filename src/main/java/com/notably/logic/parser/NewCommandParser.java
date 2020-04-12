package com.notably.logic.parser;

import static com.notably.logic.parser.CliSyntax.PREFIX_JUMP;
import static com.notably.logic.parser.CliSyntax.PREFIX_TITLE;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.notably.commons.LogsCenter;
import com.notably.commons.path.AbsolutePath;
import com.notably.logic.commands.Command;
import com.notably.logic.commands.NewCommand;
import com.notably.logic.commands.OpenCommand;
import com.notably.logic.parser.exceptions.ParseException;
import com.notably.model.Model;
import com.notably.model.block.Block;
import com.notably.model.block.BlockImpl;
import com.notably.model.block.Title;

/**
 * Represents a Parser for New Command.
 */
public class NewCommandParser implements CommandParser<Command> {
    private static final String ERROR_PREFIX_MISSING = "The Prefix \"%s\" is missing. "
            + "To see list of Command format, type: help";
    private final Logger logger = LogsCenter.getLogger(NewCommandParser.class);

    private Model notablyModel;

    public NewCommandParser(Model notablyModel) {
        this.notablyModel = notablyModel;
    }
    /**
     * Parse input and create NewCommand and OpenCommand.
     * @param args parse userInput used to create block.
     * @return List of command to execute.
     * @throws ParseException when input is invalid.
     */
    public List<Command> parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_TITLE, PREFIX_JUMP);

        if (!ParserUtil.arePrefixesPresent(argMultimap, PREFIX_TITLE)
                || !argMultimap.getPreamble().isEmpty()) {
            logger.warning(String.format("The Prefix '%s' is missing. ", PREFIX_TITLE));
            throw new ParseException(String.format(ERROR_PREFIX_MISSING, PREFIX_TITLE));
        }

        String title = argMultimap.getValue(PREFIX_TITLE).get();

        Block block;
        try {
            block = new BlockImpl(new Title(title));
        } catch (IllegalArgumentException ex) {
            logger.warning(String.format("The title '%s' provided is invalid. ", title));
            throw new ParseException(ex.getMessage());
        }

        List<Command> commands = new ArrayList<>();
        commands.add(new NewCommand(block));
        logger.info(String.format("NewCommand for the block titled '%s' created", title));

        if (!ParserUtil.arePrefixesPresent(argMultimap, PREFIX_JUMP)) {
            return commands;
        }

        AbsolutePath path = ParserUtil.createAbsolutePath(title, notablyModel.getCurrentlyOpenPath());
        commands.add(new OpenCommand(path));
        logger.info("Command jumps to the block after creation");
        return commands;
    }

}
