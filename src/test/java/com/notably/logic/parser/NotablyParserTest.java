package com.notably.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.notably.commons.parser.ParserUtil;
import com.notably.commons.path.AbsolutePath;
import com.notably.logic.commands.Command;
import com.notably.logic.commands.DeleteCommand;
import com.notably.logic.commands.EditCommand;
import com.notably.logic.commands.ExitCommand;
import com.notably.logic.commands.HelpCommand;
import com.notably.logic.commands.NewCommand;
import com.notably.logic.commands.OpenCommand;
import com.notably.logic.commands.exceptions.CommandException;
import com.notably.model.Model;
import com.notably.model.ModelManager;
import com.notably.model.block.BlockModel;
import com.notably.model.suggestion.SuggestionModel;
import com.notably.model.suggestion.SuggestionModelImpl;
import com.notably.model.viewstate.ViewStateModel;
import com.notably.model.viewstate.ViewStateModelImpl;
import com.notably.testutil.TypicalBlockModel;

public class NotablyParserTest {
    private static Model model;
    private static NotablyParser parser;

    @BeforeEach
    public void setUp() {
        // Set up model
        BlockModel blockModel = TypicalBlockModel.getTypicalBlockModel();
        SuggestionModel suggestionModel = new SuggestionModelImpl();
        ViewStateModel viewStateModel = new ViewStateModelImpl();
        model = new ModelManager(blockModel, suggestionModel, viewStateModel);

        parser = new NotablyParser(model);
    }


    @Test
    public void parseCommand_newCommandInput_newCommand() throws Exception {
        Command command = parser.parseCommand("new -t 1234").get(0);

        assertTrue(command instanceof NewCommand);

        command.execute(model);

        assertTrue(model.hasPath(ParserUtil.createAbsolutePath("1234", model.getCurrentlyOpenPath())));
    }

    @Test
    public void parseCommand_existingNoteInput_throwsError() throws Exception {
        Command command = parser.parseCommand("new -t CS2106").get(0);

        assertTrue(command instanceof NewCommand);

        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void parseCommand_newCommandInputJump_false() throws Exception {
        List<? extends Command> commands = parser.parseCommand("new -t CS2101");

        AbsolutePath expectedCurrentPath = AbsolutePath.fromString("/Y2S2");
        AbsolutePath expectedCreatedPath = AbsolutePath.fromString("/Y2S2/CS2101");

        assertTrue(commands.size() == 1);

        for (Command command : commands) {
            command.execute(model);
        }

        assertEquals(expectedCurrentPath, model.currentlyOpenPathProperty().getValue());
        assertTrue(model.hasPath(expectedCreatedPath));
    }

    @Test
    public void parseCommand_newCommandInputJump_true() throws Exception {
        AbsolutePath expectedPath = AbsolutePath.fromString("/Y2S2/CS2101");
        List<? extends Command> commands = parser.parseCommand("new -t CS2101 -o");

        assertTrue(commands.size() == 2);

        for (Command command : commands) {
            command.execute(model);
        }

        assertEquals(expectedPath , model.currentlyOpenPathProperty().getValue());
    }

    @Test
    public void parseCommand_openCommandInput_openCommand() throws Exception {
        final Command command = parser.parseCommand("open -t CS2106").get(0);
        final AbsolutePath expectedPath = TypicalBlockModel.PATH_TO_CS2106;

        assertTrue(command instanceof OpenCommand);

        command.execute(model);

        assertEquals(expectedPath , model.getCurrentlyOpenPath());
    }

    @Test
    public void parseCommand_openCommandShorthandInput() throws Exception {
        Command command = parser.parseCommand("o -t CS2103T").get(0);

        assertTrue(command instanceof OpenCommand);

        command.execute(model);

        assertEquals(AbsolutePath.fromString("/Y2S2/CS2103T"), model.getCurrentlyOpenPath());
    }

    @Test
    public void parseCommand_deleteCommandInput_deleteBlocks() throws Exception {
        AbsolutePath toDeletePath = TypicalBlockModel.PATH_TO_CS2103T;
        Command command = parser.parseCommand("delete -t CS2103T").get(0);

        assertTrue(command instanceof DeleteCommand);

        final AbsolutePath expectedChildrenDeletion1 = TypicalBlockModel.PATH_TO_CS2103T_TUTORIAL_1;
        final AbsolutePath expectedChildrenDeletion2 = TypicalBlockModel.PATH_TO_CS2103T_TUTORIAL_2;
        final AbsolutePath expectedChildrenDeletion3 = TypicalBlockModel.PATH_TO_CS2103T_TUTORIALS;
        final AbsolutePath expectedChildrenDeletion4 = TypicalBlockModel.PATH_TO_CS2103T_LECTURES;

        command.execute(model);

        assertFalse(model.hasPath(toDeletePath));
        assertFalse(model.hasPath(expectedChildrenDeletion1));
        assertFalse(model.hasPath(expectedChildrenDeletion2));
        assertFalse(model.hasPath(expectedChildrenDeletion3));
        assertFalse(model.hasPath(expectedChildrenDeletion4));
    }

    @Test
    public void parseCommand_deleteCommandInputNoPrefix_deleteCommand() throws Exception {
        AbsolutePath toDeletePath = TypicalBlockModel.PATH_TO_CS2106_TUTORIAL_1;
        Command command = parser.parseCommand("delete CS2106/Tutorials/Tutorial 1").get(0);

        assertTrue(command instanceof DeleteCommand);

        command.execute(model);

        assertFalse(model.hasPath(toDeletePath));
    }

    @Test
    public void parseCommand_editCommandInput_editCommand() throws Exception {
        Command command = parser.parseCommand("edit").get(0);
        assertTrue(command instanceof EditCommand);

        command.execute(model);

        assertTrue(model.blockEditableProperty().getValue());
    }

    @Test
    public void parseCommand_helpCommandInput_helpCommand() throws Exception {
        Command command = parser.parseCommand("help").get(0);

        assertTrue(command instanceof HelpCommand);

        assertFalse(model.isHelpOpen());
        command.execute(model);
        assertTrue(model.isHelpOpen());
    }

    @Test
    public void parseCommand_helpCommandShorthandInput_helpCommand() throws Exception {
        Command command = parser.parseCommand("h").get(0);

        assertTrue(command instanceof HelpCommand);

        assertFalse(model.isHelpOpen());
        command.execute(model);
        assertTrue(model.isHelpOpen());
    }

    @Test
    public void parseCommand_exitCommandInput_exitCommand() throws Exception {
        Command command = parser.parseCommand("exit").get(0);

        assertTrue(command instanceof ExitCommand);
    }
}
