package com.notably.model.block;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.notably.commons.path.AbsolutePath;
import com.notably.commons.path.RelativePath;
import com.notably.model.block.exceptions.CannotModifyRootException;
import com.notably.model.block.exceptions.NoSuchBlockException;
import com.notably.testutil.TypicalBlockModel;

import javafx.collections.FXCollections;

public class BlockModelTest {
    private BlockModel blockModel;

    @BeforeEach
    public void setUpBeforeEach() {
        blockModel = TypicalBlockModel.getTypicalBlockModel();
    }

    @AfterEach
    public void afterEach() {
        blockModel = new BlockModelImpl();
    }

    @Test
    public void constructor() {
        blockModel = new BlockModelImpl();
        assertEquals(blockModel.getBlockTree()
            .getRootBlock()
            .getTreeItem()
            .getChildren(), FXCollections.emptyObservableList());
        assertEquals(blockModel.getCurrentlyOpenPath(), TypicalBlockModel.PATH_TO_ROOT);
    }

    @Test
    public void setCurrentlyOpenBlock_changePath() {
        blockModel.setCurrentlyOpenBlock(TypicalBlockModel.PATH_TO_CS2103T);
        assertEquals(blockModel.getCurrentlyOpenPath(), TypicalBlockModel.PATH_TO_CS2103T);
    }
    @Test
    public void addBlockToCurrentPath_addToPathToY2S2() {
        Block newBlock = new BlockImpl(new Title("CS2100"));
        blockModel.addBlockToCurrentPath(newBlock);
        assertTrue(blockModel.hasPath(AbsolutePath.fromString("/Y2S2/CS2100")));
    }

    @Test
    public void removeBlock_removeRoot() {
        assertThrows(CannotModifyRootException.class, () -> blockModel.removeBlock(TypicalBlockModel.PATH_TO_ROOT));
    }

    @Test
    public void removeBlock_nonRootPath() {
        blockModel.setCurrentlyOpenBlock(TypicalBlockModel.PATH_TO_CS2103T_TUTORIALS);
        blockModel.removeBlock(TypicalBlockModel.PATH_TO_CS2103T_TUTORIAL_1);
        assertEquals(blockModel.getCurrentlyOpenPath(), TypicalBlockModel.PATH_TO_CS2103T_TUTORIALS);
        assertFalse(blockModel.hasPath(TypicalBlockModel.PATH_TO_CS2103T_TUTORIAL_1));
    }

    @Test
    public void removeBlock_deleteCurrentlyOpenBlock_openParent() {
        blockModel.setCurrentlyOpenBlock(TypicalBlockModel.PATH_TO_CS2106_TUTORIAL_1);
        blockModel.removeBlock(AbsolutePath.fromRelativePath(
            RelativePath.fromString("."), blockModel.getCurrentlyOpenPath()));
        assertEquals(blockModel.getCurrentlyOpenPath(), TypicalBlockModel.PATH_TO_CS2106_TUTORIALS);
    }

    @Test
    public void removeBlock_deleteParentOfCurrentlyOpenBlock_openFirstAvailablePredecessor() {
        blockModel.setCurrentlyOpenBlock(TypicalBlockModel.PATH_TO_CS2103T_TUTORIAL_1);
        blockModel.removeBlock(TypicalBlockModel.PATH_TO_CS2103T);
        assertFalse(blockModel.hasPath(TypicalBlockModel.PATH_TO_CS2103T_TUTORIAL_1));
        assertFalse(blockModel.hasPath(TypicalBlockModel.PATH_TO_CS2103T_TUTORIAL_2));
        assertFalse(blockModel.hasPath(TypicalBlockModel.PATH_TO_CS2103T_TUTORIALS));
        assertFalse(blockModel.hasPath(TypicalBlockModel.PATH_TO_CS2103T));
        assertEquals(blockModel.getCurrentlyOpenPath(), TypicalBlockModel.PATH_TO_Y2S2);
    }

    @Test
    public void removeBlock_deleteUnrelatedBlock_currentlyOpenBlockUnchanged() {
        blockModel.setCurrentlyOpenBlock(TypicalBlockModel.PATH_TO_CS2106);
        blockModel.removeBlock(TypicalBlockModel.PATH_TO_CS2103T_TUTORIAL_1);
        assertEquals(blockModel.getCurrentlyOpenPath(), TypicalBlockModel.PATH_TO_CS2106);
        assertFalse(blockModel.hasPath(TypicalBlockModel.PATH_TO_CS2103T_TUTORIAL_1));
    }

    @Test
    public void removeBlock_deleteNonExistentDistantSibling_throws() {
        blockModel.setCurrentlyOpenBlock(TypicalBlockModel.PATH_TO_CS2106);
        assertThrows(NoSuchBlockException.class, () ->
                blockModel.removeBlock(AbsolutePath.fromRelativePath(
                RelativePath.fromString("../NonExistentBlock"), blockModel.getCurrentlyOpenPath())));
    }

    @Test
    public void updateCurrentlyOpenBlockBody_modifyRootBody_throwsCannotModifyRootException () {
        blockModel.setCurrentlyOpenBlock(TypicalBlockModel.PATH_TO_ROOT);
        assertThrows(CannotModifyRootException.class, () -> blockModel.updateCurrentlyOpenBlockBody(
                new Body("New Body")));
    }

    @Test
    public void updateCurrentlyOpenBlockBody_nonRootPath() {
        blockModel.setCurrentlyOpenBlock(TypicalBlockModel.PATH_TO_CS2106_TUTORIAL_1);
        blockModel.updateCurrentlyOpenBlockBody(new Body("Updated body"));
        assertEquals(blockModel.getBlockTree()
            .get(blockModel.getCurrentlyOpenPath())
            .getBody()
            .getText(), "Updated body");
    }
}
