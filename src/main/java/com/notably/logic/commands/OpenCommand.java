package com.notably.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import com.notably.commons.LogsCenter;
import com.notably.commons.path.AbsolutePath;
import com.notably.logic.commands.exceptions.CommandException;
import com.notably.model.Model;
import com.notably.model.block.exceptions.NoSuchBlockException;

/**
 * Represents a command that opens a path.
 */
public class OpenCommand extends Command {
    public static final String COMMAND_WORD = "open";

    private AbsolutePath path;
    private final Logger logger = LogsCenter.getLogger(getClass());

    public OpenCommand(AbsolutePath path) {
        this.path = path;
    }

    /**
     * Open block path.
     * @param notablyModel used to open path.
     */
    @Override
    public void execute(Model notablyModel) throws CommandException {
        requireNonNull(notablyModel);
        logger.info("Executing OpenCommand");
        try {
            notablyModel.setCurrentlyOpenBlock(path);
        } catch (NoSuchBlockException ex) {
            throw new CommandException(ex.getMessage());
        }
        logger.info(String.format("Current Open directory set to '%s'", this.path));
    }

}
