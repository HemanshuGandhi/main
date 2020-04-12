package com.notably.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import com.notably.commons.LogsCenter;
import com.notably.commons.path.AbsolutePath;
import com.notably.logic.commands.exceptions.CommandException;
import com.notably.model.Model;

/**
 * Represent a command that edits the block's body.
 */
public class EditCommand extends Command {
    public static final String COMMAND_WORD = "edit";

    private static final String ERROR_ROOT_MODIFICATION = "Editing the root block is forbidden.";
    private static final AbsolutePath ROOT = AbsolutePath.fromString("/");

    private final Logger logger = LogsCenter.getLogger(getClass());

    /**
     * Edit the Block body of the current directory.
     * @param notablyModel used to access the tree structure.
     */
    public void execute(Model notablyModel) throws CommandException {
        requireNonNull(notablyModel);
        logger.info("Executing EditCommand");
        if (notablyModel.getCurrentlyOpenPath().equals(ROOT)) {
            logger.warning("Editing the root's body is forbidden");
            throw new CommandException(ERROR_ROOT_MODIFICATION);
        }
        notablyModel.setBlockEditable(true);
        logger.info("Edit Modal enabled");
    }
}
