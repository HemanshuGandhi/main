package com.notably.logic.correction.distance;

/**
 * API for any algorithm that calculates the edit distance of two {@link String}s.
 */
public interface EditDistanceCalculator {
    /**
     * Calculates the edit distance between two {@link Strings}
     *
     * @param first First {@link String}
     * @param second Second {@link String}
     * @return The edit distance of {@code first} and {@code second}
     */
    int calculateDistance(String first, String second);
}

