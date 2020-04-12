package com.notably.logic.suggestion.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

import com.notably.commons.path.AbsolutePath;
import com.notably.model.Model;
import com.notably.model.block.BlockTree;
import com.notably.model.block.BlockTreeItem;
import com.notably.model.suggestion.SuggestionItem;
import com.notably.model.suggestion.SuggestionItemImpl;

/**
 * Represents a suggestion generator object to open a note.
 */
public class OpenSuggestionGenerator implements SuggestionGenerator {
    private List<AbsolutePath> paths;
    private String oldTitle;

    public OpenSuggestionGenerator(List<AbsolutePath> paths, String oldTitle) {
        Objects.requireNonNull(paths);
        Objects.requireNonNull(oldTitle);

        if (oldTitle.isBlank()) {
            throw new IllegalArgumentException("The old title must contain at least one element");
        }

        this.paths = paths;
        this.oldTitle = oldTitle;
    }

    @Override
    public void execute(Model model) {
        Objects.requireNonNull(model);

        List<AbsolutePath> possiblePaths = getPossiblePaths(paths, model);
        Collections.sort(possiblePaths);
        List<SuggestionItem> suggestions = getSuggestions(possiblePaths, model);

        model.setSuggestions(suggestions);
    }

    /**
     * Generates all possible paths from the app's {@link BlockTree}
     *
     * @return List of all possible paths.
     */
    private List<AbsolutePath> getPossiblePaths(List<AbsolutePath> paths, Model model) {
        List<AbsolutePath> possiblePaths = new ArrayList<>();

        Queue<AbsolutePath> pathQueue = new LinkedList<>();

        for (AbsolutePath path : paths) {
            pathQueue.offer(path);

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
        }
        return possiblePaths;
    }

    private List<SuggestionItem> getSuggestions(List<AbsolutePath> possiblePaths, Model model) {
        Objects.requireNonNull(possiblePaths);
        Objects.requireNonNull(model);

        return possiblePaths.stream()
                .map(path -> {
                    String displayText = path.getStringRepresentation();
                    String updatedInput = model.getInput().replace(oldTitle, displayText);
                    Runnable action = () -> {
                        model.setInput(updatedInput);
                    };
                    return new SuggestionItemImpl(displayText, action);
                })
                .collect(Collectors.toList());
    }
}
