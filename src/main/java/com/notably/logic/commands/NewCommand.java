package com.notably.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import com.notably.commons.LogsCenter;
import com.notably.logic.commands.exceptions.CommandException;
import com.notably.model.Model;
import com.notably.model.block.Block;
import com.notably.model.block.exceptions.DuplicateBlockException;

/**
 * Represent a command used to add a new block.
 */
public class NewCommand extends Command {
    public static final String COMMAND_WORD = "new";

    private final Block toAdd;
    private final Logger logger = LogsCenter.getLogger(getClass());

    public NewCommand(Block block) {
        requireNonNull(block);
        this.toAdd = block;
    }

    /**
     * Add a new block to the tree structure.
     * @param notablyModel used to access the tree structure.
     * @throws CommandException when block of the same Title is detected.
     */
    public void execute(Model notablyModel) throws CommandException {
        requireNonNull(notablyModel);
        logger.info("Executing NewCommand");
        try {
            notablyModel.addBlockToCurrentPath(toAdd);
        } catch (DuplicateBlockException ex) {
            logger.warning("Duplicate block detected.");
            throw new CommandException(ex.getMessage());
        }
        logger.info(String.format("Block titled '%s' successfully created", this.toAdd.getTitle().getText()));
    }
}
