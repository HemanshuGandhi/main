package com.notably.logic.commands.suggestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

import com.notably.commons.path.AbsolutePath;
import com.notably.logic.commands.OpenCommand;
import com.notably.logic.commands.exceptions.CommandException;
import com.notably.model.Model;
import com.notably.model.block.BlockTree;
import com.notably.model.block.BlockTreeItem;
import com.notably.model.suggestion.SuggestionItem;
import com.notably.model.suggestion.SuggestionItemImpl;

/**
 * Represents a suggestion command object to search through all the notes based on keyword.
 */
public class SearchSuggestionCommand implements SuggestionCommand {
    private String keyword;

    public SearchSuggestionCommand(String keyword) {
        Objects.requireNonNull(keyword);
        this.keyword = keyword;
    }

    @Override
    public void execute(Model model) {
        Objects.requireNonNull(model);

        List<SuggestionItem> suggestions = getSuggestions(model);
        sortSuggestions(suggestions);
        model.setSuggestions(suggestions);
    }

    /**
     * Gets the list of suggestions which contain the keyword.
     *
     * @param model The app's model.
     * @return List of SuggestionItem with a display text of the block's path and action of opening the block.
     */
    private List<SuggestionItem> getSuggestions(Model model) {
        Queue<AbsolutePath> pathQueue = new LinkedList<>();
        pathQueue.offer(AbsolutePath.fromString("/"));

        List<SuggestionItem> suggestions = new ArrayList<>();
        BlockTree blockTree = model.getBlockTree();

        while (!pathQueue.isEmpty()) {
            AbsolutePath currentPath = pathQueue.poll();

            List<BlockTreeItem> childrenBlocks = blockTree.get(currentPath).getBlockChildren();
            List<AbsolutePath> childrenPaths = childrenBlocks
                    .stream()
                    .map(item -> getPath(item, currentPath, blockTree, model, suggestions))
                    .collect(Collectors.toList());
            pathQueue.addAll(childrenPaths);
        }
        return suggestions;
    }

    private AbsolutePath getPath(BlockTreeItem item, AbsolutePath currentPath, BlockTree blockTree, Model model,
                                 List<SuggestionItem> suggestions) {
        List<String> combinedComponents = new ArrayList<>(currentPath.getComponents());
        combinedComponents.add(item.getTitle().getText());
        AbsolutePath absolutePath = AbsolutePath.fromComponents(combinedComponents);

        BlockTreeItem blockTreeItem = blockTree.get(absolutePath);
        String blockBody = blockTreeItem.getBody().getText();

        /* If a blockBody contains the keyword, we create SuggestionItem by
           counting the keyword's number of occurrences in the blockBody, setting display text,
           and setting action to open that particular block when the user chooses that suggestion. */
        String bodyLowerCase = blockBody.toLowerCase();
        if (bodyLowerCase.contains(keyword.toLowerCase())) {
            addSuggestions(bodyLowerCase, absolutePath, model, suggestions);
        }

        return absolutePath;
    }

    /**
     * Adds a Suggestion Item into the list of suggestions if it contains the keyword.
     *
     * @param bodyLowerCase The body of the block in lower case.
     * @param absolutePath The absolute path of the currently checked block.
     * @param model The app's model.
     * @param suggestions The list of suggestions.
     */
    private void addSuggestions(String bodyLowerCase, AbsolutePath absolutePath, Model model,
                                List<SuggestionItem> suggestions) {
        String[] blockBodies = bodyLowerCase.split(keyword.toLowerCase(), -1);

        int frequency = blockBodies.length - 1;
        String displayText = absolutePath.getStringRepresentation();
        Runnable action = () -> {
            try {
                OpenCommand openCommand = new OpenCommand(absolutePath);
                openCommand.execute(model);
            } catch (CommandException ex) {
                /* notes suggested will definitely be able to be opened,
                as the block actually exists. AssertionError would never be thrown */
                throw new AssertionError(ex.getMessage());
            }
        };

        SuggestionItem suggestionItem = new SuggestionItemImpl(displayText, frequency, action);
        suggestions.add(suggestionItem);
    }

    /**
     * Sorts the suggestions based on frequency.
     *
     * @param suggestions The list of SuggestionItem.
     */
    private void sortSuggestions(List<SuggestionItem> suggestions) {
        Collections.sort(suggestions, (suggestion1, suggestion2) -> {
            if (!suggestion1.getProperty("frequency").isPresent()
                    || !suggestion2.getProperty("frequency").isPresent()) {
                throw new AssertionError("All search suggestion item must contain the \"frequency\" "
                        + "property");
            }

            if (!suggestion1.getProperty("displayText").isPresent()
                    || !suggestion2.getProperty("displayText").isPresent()) {
                throw new AssertionError("All search suggestion item must contain the \"displayText\" "
                        + "property");
            }

            int frequency1 = Integer.parseInt(suggestion1.getProperty("frequency").get());
            int frequency2 = Integer.parseInt(suggestion2.getProperty("frequency").get());

            if (frequency1 == frequency2) {
                String displayText1 = suggestion1.getProperty("displayText").get();
                String displayText2 = suggestion2.getProperty("displayText").get();
                return displayText1.compareToIgnoreCase(displayText2);
            }

            return frequency1 - frequency2;
        });
    }
}
