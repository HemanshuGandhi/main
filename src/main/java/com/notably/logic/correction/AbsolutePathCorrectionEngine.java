package com.notably.logic.correction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.notably.commons.LogsCenter;
import com.notably.commons.path.AbsolutePath;
import com.notably.logic.correction.distance.EditDistanceCalculator;
import com.notably.logic.correction.distance.LevenshteinDistanceCalculator;
import com.notably.model.Model;
import com.notably.model.block.BlockTree;
import com.notably.model.block.BlockTreeItem;

/**
 * Represents a correction engine that works on {@link AbsolutePath}s
 */
public class AbsolutePathCorrectionEngine implements CorrectionEngine<AbsolutePath> {
    private static final Logger logger = LogsCenter.getLogger(AbsolutePathCorrectionEngine.class);

    private final EditDistanceCalculator editDistanceCalculator;
    private final Model model;
    private final CorrectionEngineParameters parameters;

    /**
     * Creates an {@link AbsolutePathCorrectionEngine}.
     *
     * @param model App's model
     * @param parameters Parameters for the correction engine
     */
    public AbsolutePathCorrectionEngine(Model model, CorrectionEngineParameters parameters) {
        this(new LevenshteinDistanceCalculator(false), model, parameters);
    }

    /**
     * Creates an {@link AbsolutePathCorrectionEngine}.
     *
     * @param editDistanceCalculator Edit distance calculator instance
     * @param model App's model
     * @param parameters Parameters for the correction engine
     */
    public AbsolutePathCorrectionEngine(EditDistanceCalculator editDistanceCalculator, Model model,
            CorrectionEngineParameters parameters) {
        Objects.requireNonNull(editDistanceCalculator);
        Objects.requireNonNull(model);
        Objects.requireNonNull(parameters);

        this.editDistanceCalculator = editDistanceCalculator;
        this.model = model;
        this.parameters = parameters;
    }

    /**
     * Corrects a given path.
     *
     * @param uncorrected An uncorrected path
     * @return Result of the correction
     */
    @Override
    public CorrectionResult<AbsolutePath> correct(AbsolutePath uncorrected) {
        Objects.requireNonNull(uncorrected);

        List<AbsolutePath> possiblePaths = getPossiblePaths();

        int closestDistance = Integer.MAX_VALUE;
        for (AbsolutePath possiblePath : possiblePaths) {
            int distance = calculatePathDistance(uncorrected, possiblePath);
            if (distance < closestDistance) {
                closestDistance = distance;
            }
        }

        List<AbsolutePath> correctedItems = new ArrayList<>();
        for (AbsolutePath possiblePath : possiblePaths) {
            int distance = calculatePathDistance(uncorrected, possiblePath);
            if (distance == closestDistance) {
                correctedItems.add(possiblePath);
            }
        }

        if (closestDistance > parameters.getDistanceThreshold()) {
            logger.info(String.format("Failed to correct \"%s\".", uncorrected));
            return new CorrectionResult<>(CorrectionStatus.FAILED);
        }

        if (correctedItems.size() == 1 && correctedItems.get(0).equals(uncorrected)) {
            logger.info(String.format("\"%s\" is already valid, left unchanged.", uncorrected));
            return new CorrectionResult<>(CorrectionStatus.UNCHANGED, correctedItems);
        }

        logger.info(String.format("Corrected \"%s\" to %s.", uncorrected, correctedItems));
        return new CorrectionResult<>(CorrectionStatus.CORRECTED, correctedItems);
    }

    /**
     * Generates all possible paths from the app's {@link BlockTree}
     *
     * @return List of all possible paths
     */
    private List<AbsolutePath> getPossiblePaths() {
        List<AbsolutePath> possiblePaths = new ArrayList<>();

        Queue<AbsolutePath> pathQueue = new LinkedList<>();
        pathQueue.offer(AbsolutePath.fromString("/"));

        while (!pathQueue.isEmpty()) {
            AbsolutePath currentPath = pathQueue.poll();

            List<BlockTreeItem> childrenBlocks = model.getBlockTree().get(currentPath).getBlockChildren();
            List<AbsolutePath> childrenPaths = childrenBlocks
                    .stream()
                    .map(item -> {
                        List<String> combinedComponents = new ArrayList<>(currentPath.getComponents());
                        combinedComponents.add(item.getTitle().getText());
                        return AbsolutePath.fromComponents(combinedComponents);
                    })
                    .collect(Collectors.toList());
            pathQueue.addAll(childrenPaths);

            possiblePaths.add(currentPath);
        }

        return possiblePaths;
    }

    /**
     * Calculate the edit distance between two {@link AbsolutePath}s.
     *
     * @param input Input {@link AbsolutePath}
     * @param reference Reference {@link AbsolutePath}
     * @return Edit distance between {@code input} and {@code reference}
     */
    private int calculatePathDistance(AbsolutePath input, AbsolutePath reference) {
        Objects.requireNonNull(input);
        Objects.requireNonNull(reference);

        List<String> inputComponents = input.getComponents();
        List<String> referenceComponents = reference.getComponents();

        // Calculate the cumulative distance between the two paths component-by-component
        int i = 0;
        int distance = 0;
        while (i < inputComponents.size() && i < referenceComponents.size()) {
            String inputComponent = inputComponents.get(i);
            String referenceComponent = referenceComponents.get(i);

            if (parameters.isForwardMatching()
                    && i == inputComponents.size() - 1
                    && inputComponent.length() <= referenceComponent.length()) {
                distance += CorrectionEngineUtil.calculateForwardMatchingDistance(editDistanceCalculator,
                        inputComponent, referenceComponent, parameters.getForwardMatchingThreshold());
            } else {
                distance += editDistanceCalculator.calculateDistance(inputComponent, referenceComponent);
            }

            i++;
        }

        // If one path is longer than another, increase cumulative distance by the size of
        // each extra component's length.
        while (i < inputComponents.size()) {
            distance += inputComponents.get(i).length();
            i++;
        }
        while (i < referenceComponents.size()) {
            distance += referenceComponents.get(i).length();
            i++;
        }

        return distance;
    }
}

