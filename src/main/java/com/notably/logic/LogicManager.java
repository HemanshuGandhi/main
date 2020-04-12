package com.notably.logic;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import com.notably.commons.GuiSettings;
import com.notably.commons.LogsCenter;
import com.notably.logic.commands.Command;
import com.notably.logic.commands.exceptions.CommandException;
import com.notably.logic.exceptions.EditBlockBodyException;
import com.notably.logic.parser.NotablyParser;
import com.notably.logic.parser.exceptions.ParseException;
import com.notably.logic.suggestion.SuggestionEngine;
import com.notably.model.Model;
import com.notably.model.block.Body;
import com.notably.model.block.exceptions.CannotModifyRootException;
import com.notably.storage.Storage;


/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_MESSAGE = "Could not save data to file: ";
    private static final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final NotablyParser notablyParser;
    private final SuggestionEngine suggestionEngine;

    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        notablyParser = new NotablyParser(model);
        suggestionEngine = new SuggestionEngine(model);
    }

    @Override
    public void execute(String commandText) throws CommandException, ParseException {
        logger.info(String.format("User inputted '%s'", commandText));
        try {
            List<? extends Command> commands = notablyParser.parseCommand(commandText);
            for (Command command : commands) {
                command.execute(model);
            }
            storage.saveBlockModel(model);

        } catch (IOException ioe) {
            model.setResponseText(FILE_OPS_ERROR_MESSAGE + ioe);
            throw new CommandException(FILE_OPS_ERROR_MESSAGE + ioe, ioe);
        } catch (CommandException | ParseException ex) {
            model.setResponseText(ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void editCurrentBlockBody(String bodyContent) throws EditBlockBodyException {
        requireNonNull(bodyContent);
        Body body = new Body(bodyContent);

        // Throws exception if current block is a Root.
        try {
            model.updateCurrentlyOpenBlockBody(body);
        } catch (CannotModifyRootException ex) {
            throw new EditBlockBodyException(ex.getMessage());
        }
    }

    @Override
    public Path getBlockDataFilePath() {
        return model.getBlockDataFilePath();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
