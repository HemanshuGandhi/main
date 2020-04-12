package com.notably.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import com.notably.commons.LogsCenter;
import com.notably.commons.exceptions.DataConversionException;
import com.notably.model.block.BlockModel;
import com.notably.model.userpref.ReadOnlyUserPrefModel;
import com.notably.model.userpref.UserPrefModel;

/**
 * Manages storage of BlockModel data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private BlockStorage blockStorage;
    private UserPrefsStorage userPrefsStorage;


    public StorageManager(BlockStorage blockStorage, UserPrefsStorage userPrefsStorage) {
        super();
        this.blockStorage = blockStorage;
        this.userPrefsStorage = userPrefsStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefModel> readUserPrefs() throws DataConversionException, IOException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefModel userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ BlockModel methods ==============================

    @Override
    public Path getBlockDataFilePath() {
        return blockStorage.getBlockDataFilePath();
    }

    @Override
    public Optional<BlockModel> readBlockModel() throws DataConversionException, IOException {
        return readBlockModel(blockStorage.getBlockDataFilePath());
    }

    @Override
    public Optional<BlockModel> readBlockModel(Path filePath) throws DataConversionException, IOException {
        logger.fine("Attempting to read data from file: " + filePath);
        return blockStorage.readBlockModel(filePath);
    }

    @Override
    public void saveBlockModel(BlockModel blockModel) throws IOException {
        saveBlockModel(blockModel, blockStorage.getBlockDataFilePath());
    }

    @Override
    public void saveBlockModel(BlockModel blockModel, Path filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        blockStorage.saveBlockModel(blockModel, filePath);
    }

}
