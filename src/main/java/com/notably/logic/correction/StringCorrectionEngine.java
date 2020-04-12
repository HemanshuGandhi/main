package com.notably.logic.correction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import com.notably.commons.LogsCenter;
import com.notably.logic.correction.distance.EditDistanceCalculator;
import com.notably.logic.correction.distance.LevenshteinDistanceCalculator;

/**
 * Represents a correction engine that works on {@link String}s.
 */
public class StringCorrectionEngine implements CorrectionEngine<String> {
    private static final Logger logger = LogsCenter.getLogger(StringCorrectionEngine.class);

    private final EditDistanceCalculator editDistanceCalculator;
    private final List<String> options;
    private final CorrectionEngineParameters parameters;

    /**
     * Creates a {@link StringCorrectionEngine}.
     *
     * @param options Correction options
     * @param parameters Parameters for the correction engine
     */
    public StringCorrectionEngine(List<String> options, CorrectionEngineParameters parameters) {
        this(new LevenshteinDistanceCalculator(false), options, parameters);
    }

    /**
     * Creates a {@link StringCorrectionEngine}.
     *
     * @param editDistanceCalculator Edit distance calculator instance
     * @param options Correction options
     * @param parameters Parameters for the correction engine
     */
    public StringCorrectionEngine(EditDistanceCalculator editDistanceCalculator, List<String> options,
            CorrectionEngineParameters parameters) {
        Objects.requireNonNull(editDistanceCalculator);
        Objects.requireNonNull(options);
        Objects.requireNonNull(parameters);

        if (options.isEmpty()) {
            throw new IllegalArgumentException("\"options\" must contain at least one element");
        }

        this.editDistanceCalculator = editDistanceCalculator;
        this.options = options;
        this.parameters = parameters;
    }

    /**
     * Corrects a given string.
     *
     * @param uncorrected Uncorrected string
     * @return Result of the correction
     */
    @Override
    public CorrectionResult<String> correct(String uncorrected) {
        Objects.requireNonNull(uncorrected);

        int closestDistance = Integer.MAX_VALUE;
        for (String option : options) {
            int distance = calculateStringDistance(uncorrected, option);
            if (distance < closestDistance) {
                closestDistance = distance;
            }
        }

        List<String> correctedItems = new ArrayList<>();
        for (String option : options) {
            int distance = calculateStringDistance(uncorrected, option);
            if (distance == closestDistance) {
                correctedItems.add(option);
            }
        }

        if (closestDistance > parameters.getDistanceThreshold()) {
            logger.info(String.format("Failed to correct \"%s\".", uncorrected));
            return new CorrectionResult<>(CorrectionStatus.FAILED);
        }

        if (correctedItems.size() == 1 && correctedItems.get(0).equalsIgnoreCase(uncorrected)) {
            logger.info(String.format("\"%s\" is already valid, left unchanged.", uncorrected));
            return new CorrectionResult<>(CorrectionStatus.UNCHANGED, correctedItems);
        }

        logger.info(String.format("Corrected \"%s\" to %s.", uncorrected, correctedItems));
        return new CorrectionResult<String>(CorrectionStatus.CORRECTED, correctedItems);
    }

    /**
     * Calculates the edit distance between two {@link String}s.
     *
     * @param input Input string
     * @param reference Reference string
     * @return Edit distance between {@code input} and {@code reference}
     */
    private int calculateStringDistance(String input, String reference) {
        Objects.requireNonNull(input);
        Objects.requireNonNull(reference);

        int distance;
        if (parameters.isForwardMatching() && input.length() <= reference.length()) {
            distance = CorrectionEngineUtil.calculateForwardMatchingDistance(editDistanceCalculator,
                    input, reference, parameters.getForwardMatchingThreshold());
        } else {
            distance = editDistanceCalculator.calculateDistance(input, reference);
        }

        return distance;
    }
}

