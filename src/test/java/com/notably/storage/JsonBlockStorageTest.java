package com.notably.storage;

import static com.notably.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.notably.commons.exceptions.DataConversionException;
import com.notably.commons.path.AbsolutePath;
import com.notably.model.block.BlockModel;
import com.notably.model.block.BlockModelImpl;
import com.notably.testutil.TypicalBlockModel;

public class JsonBlockStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonBlockStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readBlockModel_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readBlockModel(null));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    private java.util.Optional<BlockModel> readBlockModel(String filePath) throws Exception {
        return new JsonBlockStorage(Paths.get(filePath)).readBlockModel(addToTestDataPathIfNotNull(filePath));
    }

    /**
     * Saves the {@code BlockModel} at the specified {@code filePath}.
     */
    private void saveBlockModel(BlockModel blockModel, String filePath) {
        try {
            new JsonBlockStorage(Paths.get(filePath))
                    .saveBlockModel(blockModel, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readBlockModel("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataConversionException.class, () -> readBlockModel("notJsonFormatBlockData.json"));
    }

    @Test
    public void readBlockModel_invalidBlockModel_throwDataConversionException() {
        assertThrows(DataConversionException.class, () -> readBlockModel("invalidBlockData.json"));
    }

    @Test
    public void readBlockModel_invalidAndValidBlockModel_throwDataConversionException() {
        assertThrows(DataConversionException.class, () -> readBlockModel("invalidAndValidBlockData.json"));
    }

    @Test
    public void readAndSaveBlockModel_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempBlockModel.json");
        BlockModel original = TypicalBlockModel.getTypicalBlockModel();
        JsonBlockStorage jsonBlockStorage = new JsonBlockStorage(filePath);

        // Save in new file and read back
        jsonBlockStorage.saveBlockModel(original);
        BlockModel readBack = jsonBlockStorage.readBlockModel(filePath).get();
        assertEquals(original.getBlockTree(), readBack.getBlockTree());
        assertEquals(original.getCurrentlyOpenPath(), readBack.getCurrentlyOpenPath());

        // Modify data, overwrite exiting file, and read back
        original.getBlockTree().add(AbsolutePath.fromString("/Y2S2"), TypicalBlockModel.CS2107);
        jsonBlockStorage.saveBlockModel(original, filePath);
        readBack = jsonBlockStorage.readBlockModel(filePath).get();
        assertEquals(original.getBlockTree(), readBack.getBlockTree());
        assertEquals(original.getCurrentlyOpenPath(), readBack.getCurrentlyOpenPath());

        // Save and read without specifying file path
        original.getBlockTree().add(AbsolutePath.fromString("/Y2S2"), TypicalBlockModel.CS3230);
        jsonBlockStorage.saveBlockModel(original); // file path not specified
        readBack = jsonBlockStorage.readBlockModel().get(); // file path not specified
        assertEquals(original.getBlockTree(), readBack.getBlockTree());
        assertEquals(original.getCurrentlyOpenPath(), readBack.getCurrentlyOpenPath());
    }

    @Test
    public void saveBlockModel_nullBlockModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveBlockModel(null, "SomeFile.json"));
    }

    @Test
    public void saveBlockModel_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveBlockModel(new BlockModelImpl(), null));
    }
}
