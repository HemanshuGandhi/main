package com.notably.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import com.notably.commons.LogsCenter;
import com.notably.model.Model;

/**
 * Represent a command that enable/disables the help window.
 */
public class HelpCommand extends Command {
    public static final String COMMAND_WORD = "help";

    private final Logger logger = LogsCenter.getLogger(getClass());

    @Override
    public void execute(Model notablyModel) {
        requireNonNull(notablyModel);
        logger.info("Executing HelpCommand");
        notablyModel.setHelpOpen(true);
        logger.info("Help Modal Enabled");
    }
}
