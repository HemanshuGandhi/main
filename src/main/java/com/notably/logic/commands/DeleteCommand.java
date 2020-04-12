package com.notably.logic.commands;

import static java.util.Objects.requireNonNull;

import com.notably.commons.path.AbsolutePath;
import com.notably.commons.path.Path;
import com.notably.commons.path.RelativePath;
import com.notably.logic.commands.exceptions.CommandException;
import com.notably.model.Model;
import com.notably.model.block.exceptions.CannotModifyRootException;
import com.notably.model.block.exceptions.NoSuchBlockException;

/**
 * Represents a command that deletes a block.
 */
public class DeleteCommand extends Command {
    public static final String COMMAND_WORD = "delete";
    private Path targetPath;

    public DeleteCommand(Path targetPath) {
        this.targetPath = targetPath;
    }

    @Override
    public void execute(Model notablyModel) throws CommandException {
        requireNonNull(notablyModel);
        if (this.targetPath instanceof RelativePath) {
            RelativePath toConvert = (RelativePath) this.targetPath;
            this.targetPath = toConvert.toAbsolutePath(notablyModel.getCurrentlyOpenPath());
        }

        try {
            notablyModel.removeBlock((AbsolutePath) this.targetPath);
        } catch (NoSuchBlockException | CannotModifyRootException ex) {
            throw new CommandException(ex.getMessage());
        }
    }


}
