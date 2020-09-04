package com.andreaseisele.zettel.core.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Zipper {

    private static final Logger logger = LoggerFactory.getLogger(Zipper.class);

    public static void unzip(Path zip, Path target) throws IOException {
        logger.debug("unzipping {} into {}", zip, target);

        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(zip))) {
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null) {
                final Path entryPath = target.resolve(entry.getName());

                if (entry.isDirectory()) {
                    logger.debug("entry {} is directory, creating {}", entry, entryPath);
                    Files.createDirectories(entryPath);

                } else {
                    final Path dir = entryPath.getParent();
                    logger.debug("entry is file, inflating {} to {}", entry, dir);
                    Files.createDirectories(dir);
                    Files.copy(zin, entryPath);
                }
            }
        }
    }

}
