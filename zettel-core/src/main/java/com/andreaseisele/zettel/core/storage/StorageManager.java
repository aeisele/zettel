package com.andreaseisele.zettel.core.storage;

import net.harawata.appdirs.AppDirsFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

@Singleton
public class StorageManager {

    private static final String APP_NAME = "zettelwirtschaft";

    private final FileSystem fileSystem;

    @Inject
    public StorageManager(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public Path getApplicationDirectory() throws IOException {
        String userDataDir = AppDirsFactory.getInstance().getUserDataDir(APP_NAME, null, null);
        Path path = fileSystem.getPath(userDataDir);
        Files.createDirectories(path);
        return path;
    }

    public Path getApplicationSubDirectory(String directory) throws IOException {
        Path applicationDirectory = getApplicationDirectory();
        Path subDirectory = applicationDirectory.resolve(directory);
        Files.createDirectories(subDirectory);
        return subDirectory;
    }

}
