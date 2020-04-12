package com.notably.view.blockcontent;

import static com.notably.commons.util.CollectionUtil.requireAllNonNull;

import com.notably.logic.Logic;
import com.notably.model.Model;

import javafx.scene.layout.VBox;

/**
 * View of the currently open block's content.
 */
public class BlockContent {

    private final BlockContentDisplayView blockContentDisplayView;
    private final BlockContentEditView blockContentEditView;

    public BlockContent(VBox blockContentPlaceholder, Logic logic, Model model) {
        requireAllNonNull(blockContentPlaceholder, model, logic);

        blockContentDisplayView = new BlockContentDisplayView(model);
        blockContentEditView = new BlockContentEditView(logic, model);

        blockContentPlaceholder.getChildren().add(blockContentDisplayView.getRoot());
    }
}

