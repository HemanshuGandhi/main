package com.notably.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import com.notably.commons.LogsCenter;
import com.notably.logic.parser.exceptions.ParseException;
import com.notably.model.Model;

/**
 * Parses Search input arguments
 */
public class SearchCommandParser {
    public static final String COMMAND_WORD = "search";

    private static final String SELECT_ONE = "Please select one of the suggestions below, then press enter!";
    private static final String NO_SUGGESTION = "Unable to search for notes containing the keyword '%s'";
    private final Logger logger = LogsCenter.getLogger(SearchCommandParser.class);

    private Model notablyModel;

    public SearchCommandParser(Model notablyModel) {
        this.notablyModel = notablyModel;
    }
    /**
     * Parses the given {@code String} of arguments in the context of the SearchCommand
     * @throws ParseException is always thrown, error message is determined by user input.
     */
    public void parse(String args) throws ParseException {
        requireNonNull(args);
        if (notablyModel.getSuggestions().isEmpty()) {
            logger.warning("Suggestion does not exist");
            throw new ParseException(String.format(NO_SUGGESTION, args));
        }

        logger.warning("User pressed enter without selecting a suggestion");
        throw new ParseException(SELECT_ONE);
    }
}
