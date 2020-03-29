package com.notably.commons.compiler.tokenizer.rule;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.notably.commons.compiler.tokenizer.Token;

/**
 * Represents a rule that dictates how to convert a single line feed character into a line feed {@link Token}.
 */
public class LineFeedRule implements Rule {
    private static final Pattern PATTERN = Pattern.compile("^\\n");

    @Override
    public Optional<Token> extractFront(String input) {
        Matcher matcher = PATTERN.matcher(input);
        if (!matcher.find()) {
            return Optional.empty();
        }
        return Optional.of(Token.LF);
    }
}
