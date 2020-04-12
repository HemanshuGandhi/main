package com.notably.logic.correction.distance;

import java.util.Objects;
import java.util.logging.Logger;

import com.notably.commons.LogsCenter;

/**
 * An implementation of the Levenshtein algorithm for calculating the edit distance of two {@link Strings}.
 * Inspired by https://web.stanford.edu/class/cs124/lec/med.pdf.
 */
public class LevenshteinDistanceCalculator implements EditDistanceCalculator {
    private static final Logger logger = LogsCenter.getLogger(LevenshteinDistanceCalculator.class);

    private final boolean isCaseSensitive;

    /**
     * Creates a {@link LevenshteinDistanceCalculator} instance
     *
     * @param isCaseSensitive Whether or not edit distance should account for case difference
     */
    public LevenshteinDistanceCalculator(boolean isCaseSensitive) {
        this.isCaseSensitive = isCaseSensitive;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int calculateDistance(String first, String second) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);

        // Uses dynamic programming bottom-up approach in calculating the edit distance.
        // For each pair of i,j, distance[i][j] represents the edit distance between
        // first[0...i] and second[0...j].

        int[][] distance = new int[first.length() + 1][second.length() + 1];

        for (int i = 0; i <= first.length(); i++) {
            distance[i][0] = i;
        }
        for (int j = 0; j <= second.length(); j++) {
            distance[0][j] = j;
        }

        for (int i = 1; i <= first.length(); i++) {
            for (int j = 1; j <= second.length(); j++) {
                char firstChar = first.charAt(i - 1);
                char secondChar = second.charAt(j - 1);
                if (!isCaseSensitive) {
                    firstChar = Character.toLowerCase(firstChar);
                    secondChar = Character.toLowerCase(secondChar);
                }

                if (firstChar == secondChar) {
                    distance[i][j] = min(
                            distance[i - 1][j] + 1,
                            distance[i][j - 1] + 1,
                            distance[i - 1][j - 1]);
                } else {
                    distance[i][j] = min(
                            distance[i - 1][j] + 1,
                            distance[i][j - 1] + 1,
                            distance[i - 1][j - 1] + 2);
                }
            }
        }

        logger.fine(String.format("Edit distance between \"%s\" and \"%s\" is %d", first, second,
                distance[first.length()][second.length()]));

        return distance[first.length()][second.length()];
    }

    /**
     * Calculates the minimum value of three integers.
     *
     * @param a First integer
     * @param b Second integer
     * @param c Third integer
     * @return The mininum of the three integers
     */
    private int min(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }
}

