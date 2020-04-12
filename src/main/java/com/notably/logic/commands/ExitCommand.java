package com.notably.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import com.notably.commons.LogsCenter;
import com.notably.model.Model;

import javafx.application.Platform;

/**
 * Represent a command that exits the program.
 */
public class ExitCommand extends Command {
    public static final String COMMAND_WORD = "exit";

    private final Logger logger = LogsCenter.getLogger(getClass());

    @Override
    public void execute(Model notablyModel) {
        requireNonNull(notablyModel);
        logger.info("Executing ExitCommand");
        Platform.exit();
    }
}
