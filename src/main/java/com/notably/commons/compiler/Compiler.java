package com.notably.commons.compiler;

import java.util.logging.Logger;

import com.notably.commons.LogsCenter;
import com.notably.commons.compiler.parser.Parser;

/**
 * A Markdown to HTML compiler.
 */
public class Compiler {
    private static final Logger logger = LogsCenter.getLogger(Compiler.class);

    /**
     * Compiles a Markdown string to an HTML string.
     *
     * @param markdown Markdown string
     * @return HTML string
     */
    public static String compile(String markdown) {
        logger.fine(String.format("Compiling the following Markdown into HTML:\n%s\n", markdown));
        return Parser.parse(markdown).toHtml();
    }
}

