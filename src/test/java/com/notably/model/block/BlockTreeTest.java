package com.notably.model.block;

import static com.notably.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.notably.commons.path.AbsolutePath;
import com.notably.model.block.exceptions.CannotModifyRootException;
import com.notably.model.block.exceptions.DuplicateBlockException;
import com.notably.model.block.exceptions.NoSuchBlockException;
import com.notably.testutil.TypicalBlockModel;

public class BlockTreeTest {

    private BlockTree blockTree = new BlockTreeImpl();

    @BeforeEach
    private void setUpTestTree() {
        blockTree = TypicalBlockModel.getTypicalBlockModel().getBlockTree();
    }

    @AfterEach
    private void tearDownTestTree() {
        blockTree = new BlockTreeImpl();
    }

    @Test
    public void constructor() {
        assertTrue(blockTree.getRootBlock().isRootBlock());
    }

    @Test
    public void get_root() {
        assertEquals(blockTree.get(TypicalBlockModel.PATH_TO_ROOT), blockTree.getRootBlock());
    }

    @Test
    public void get_pathDoesNotExist_throwsNoSuchBlockException() {
        AbsolutePath nonExistentPath = AbsolutePath.fromString("/SomeNonExistentPath");
        assertThrows(NoSuchBlockException.class, () -> blockTree.get(nonExistentPath));
    }

    @Test
    public void add_addNewBlock() {
        Block newBlock = new BlockImpl(new Title("CS2101"));
        AbsolutePath toCs2103Week2 = AbsolutePath.fromString("/Y2S2/CS2101");
        blockTree.add(TypicalBlockModel.PATH_TO_Y2S2, newBlock);
        assertEquals(blockTree.get(toCs2103Week2).getBlock(), newBlock);
    }

    @Test
    public void add_addDuplicateBlock_throwsDuplicateBlockException() {
        Block newBlock = new BlockImpl(new Title("Y2S2"));
        assertThrows(DuplicateBlockException.class, () -> blockTree.add(TypicalBlockModel.PATH_TO_ROOT, newBlock));
    }

    @Test
    public void add_addDeeperNestedPath() {
        Block newBlock = new BlockImpl(new Title("Labs"));
        blockTree.add(TypicalBlockModel.PATH_TO_CS2103T, newBlock);
        assertEquals(blockTree.get(AbsolutePath.fromString("/Y2S2/CS2103T/Labs")).getBlock(), newBlock);
    }

    @Test
    public void remove_removeBlockAndChildren_throwsNoSuchBlockException() {
        blockTree.remove(TypicalBlockModel.PATH_TO_CS2103T);
        assertThrows(NoSuchBlockException.class, () -> blockTree.get(TypicalBlockModel.PATH_TO_CS2103T));
        assertThrows(NoSuchBlockException.class, () -> blockTree.get(TypicalBlockModel.PATH_TO_CS2103T_TUTORIAL_1));
    }

    @Test
    public void remove_removeRoot_throwsCannotModifyRootException() {
        assertThrows(CannotModifyRootException.class, () -> blockTree.remove(TypicalBlockModel.PATH_TO_ROOT));
    }

    @Test
    public void set_modifyRootBody_throwsCannotModifyRootException() {
        Block newBlock = new BlockImpl(new Title("NUS"));
        assertThrows(CannotModifyRootException.class, () -> blockTree.set(TypicalBlockModel.PATH_TO_ROOT, newBlock));
    }

    @Test
    public void set_block_editBlock() {
        Block editedBlock = new BlockImpl(new Title("Tutorial 3"), new Body("Was 'Tutorial 1'"));
        AbsolutePath editedPath = AbsolutePath.fromString("/Y2S2/CS2103T/Tutorials/Tutorial 3");
        blockTree.set(TypicalBlockModel.PATH_TO_CS2103T_TUTORIAL_1, editedBlock);
        assertEquals(blockTree.get(editedPath).getBlock(), editedBlock);
    }

    @Test
    public void testRootBodyContent() {
        blockTree = new BlockTreeImpl();
        assertEquals(blockTree.getRootBlock().getBody().getText(), "Create a new note to get started!");

        blockTree.add(TypicalBlockModel.PATH_TO_ROOT, TypicalBlockModel.Y2S2);
        assertEquals(blockTree.getRootBlock().getBody().getText(), "Open a note to get started!");

        blockTree.remove(TypicalBlockModel.PATH_TO_Y2S2);
        assertEquals(blockTree.getRootBlock().getBody().getText(), "Create a new note to get started!");
    }
}
