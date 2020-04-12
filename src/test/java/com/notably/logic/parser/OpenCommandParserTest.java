package com.notably.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.notably.commons.path.AbsolutePath;
import com.notably.logic.commands.OpenCommand;
import com.notably.logic.commands.exceptions.CommandException;
import com.notably.logic.correction.AbsolutePathCorrectionEngine;
import com.notably.logic.correction.CorrectionEngine;
import com.notably.logic.correction.CorrectionEngineParameters;
import com.notably.logic.parser.exceptions.ParseException;
import com.notably.model.Model;
import com.notably.model.ModelManager;
import com.notably.model.block.BlockModel;
import com.notably.model.suggestion.SuggestionModel;
import com.notably.model.suggestion.SuggestionModelImpl;
import com.notably.model.viewstate.ViewStateModel;
import com.notably.model.viewstate.ViewStateModelImpl;
import com.notably.testutil.TypicalBlockModel;

class OpenCommandParserTest {
    private static Model model;
    private static OpenCommandParser openCommandParser;

    @BeforeEach
    public void setUp() {
        // Set up model
        BlockModel blockModel = TypicalBlockModel.getTypicalBlockModel();
        SuggestionModel suggestionModel = new SuggestionModelImpl();
        ViewStateModel viewStateModel = new ViewStateModelImpl();
        model = new ModelManager(blockModel, suggestionModel, viewStateModel);
        CorrectionEngine<AbsolutePath> pathCorrectionEngine = new AbsolutePathCorrectionEngine(model,
                new CorrectionEngineParameters().setForwardMatching(false).setDistanceThreshold(2));
        openCommandParser = new OpenCommandParser(model, pathCorrectionEngine);
    }

    @Test
    void parse_emptyArgument_throwParseException() throws ParseException {
        assertThrows(ParseException.class, () -> openCommandParser.parse(""));
    }

    @Test
    void parse_emptyTitleArgument_throwParseException() throws ParseException {
        assertThrows(ParseException.class, () -> openCommandParser.parse("-t"));
    }

    @Test
    void parse_rootTitleArgument_throwParseException() throws ParseException {
        assertThrows(ParseException.class, () -> openCommandParser.parse("-t /"));
    }

    @Test
    void parse_invalidPathArgument_throwParseException() throws ParseException {
        assertThrows(ParseException.class, () -> openCommandParser.parse("-t /nonExisting"));
    }

    @Test
    void parse_absolutePathArgument_openCommand() throws ParseException, CommandException {
        final AbsolutePath toOpenPath = TypicalBlockModel.PATH_TO_CS2106;
        final OpenCommand openCommand = openCommandParser.parse(" -t /Y2S2/CS2106").get(0);

        openCommand.execute(model);

        assertEquals(toOpenPath, model.currentlyOpenPathProperty().getValue());
    }

    @Test
    void parse_absolutePathArgumentTypo_openCommand() throws ParseException, CommandException {
        final AbsolutePath toOpenPath = TypicalBlockModel.PATH_TO_CS2106;
        final OpenCommand openCommand = openCommandParser.parse(" -t /Y2S1/CS2106").get(0);

        openCommand.execute(model);

        assertEquals(toOpenPath, model.currentlyOpenPathProperty().getValue());
    }

    @Test
    void parse_relativePathArgument_openCommand() throws ParseException, CommandException {
        final AbsolutePath toOpenPath = TypicalBlockModel.PATH_TO_CS2106;
        final OpenCommand openCommand = openCommandParser.parse(" -t CS2106").get(0);

        // Current directory `/Y2S2`
        openCommand.execute(model);

        //Expected directory after executing Command /another/block
        assertEquals(toOpenPath, model.currentlyOpenPathProperty().getValue());
    }

    @Test
    void parse_relativePathArgumentTypo_openCommand() throws ParseException, CommandException {
        final AbsolutePath toOpenPath = TypicalBlockModel.PATH_TO_CS2106;
        final OpenCommand openCommand = openCommandParser.parse(" -t /Y2S2/CS210").get(0);

        // Current directory `/Y2S2`
        openCommand.execute(model);

        //Expected directory after executing Command /another/block
        assertEquals(toOpenPath, model.currentlyOpenPathProperty().getValue());
    }
}
