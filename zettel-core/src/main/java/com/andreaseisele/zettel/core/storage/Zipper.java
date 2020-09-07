package com.andreaseisele.zettel.core.storage;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Zipper {

    private static final int BUFFER_SIZE = 1024 * 1024 * 64; // 64 MB

    private static final Logger logger = LoggerFactory.getLogger(Zipper.class);

    public static void unzip(Path zip, Path target) throws IOException {
        logger.debug("unzipping {} into {}", zip, target);

        try (ZipInputStream zin = new ZipInputStream(new BufferedInputStream(Files.newInputStream(zip)))) {
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

    public static void unSevenZip(Path sevenZip, Path target) throws IOException {
        logger.debug("un7zipping {} into {}", sevenZip, target);

        try (SevenZFile sevenZFile = new SevenZFile(sevenZip.toFile())) {
            SevenZArchiveEntry entry;
            while ((entry = sevenZFile.getNextEntry()) != null) {
                final Path entryPath = target.resolve(entry.getName());

                if (entry.isDirectory()) {
                    logger.debug("entry {} is directory, creating {}", entry, entryPath);
                    Files.createDirectories(entryPath);

                } else {
                    final Path dir = entryPath.getParent();
                    logger.debug("entry is file, inflating {} to {}", entry, dir);
                    Files.createDirectories(dir);
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int length;
                    try (OutputStream out = Files.newOutputStream(entryPath)) {
                        while ((length = sevenZFile.read(buffer)) != -1) {
                            out.write(buffer, 0, length);
                        }
                    }
                }
            }
        }
    }

    public static void unXzip(Path xzip, Path target) throws IOException {
        logger.debug("unxzipping {} to {}", xzip, target);

        try (final XZCompressorInputStream xin =
                     new XZCompressorInputStream(new BufferedInputStream(Files.newInputStream(xzip)))) {
            Files.copy(xin, target);
        }
    }

    public static void unTar(Path tar, Path target) throws IOException {
        logger.debug("untaring {} to {}", tar, target);

        try (TarArchiveInputStream tin =
                     new TarArchiveInputStream(new BufferedInputStream(Files.newInputStream(tar)))) {
            TarArchiveEntry entry;
            while ((entry = tin.getNextTarEntry()) != null) {
                final Path entryPath = target.resolve(entry.getName());

                if (entry.isDirectory()) {
                    logger.debug("entry {} is directory, creating {}", entry, entryPath);
                    Files.createDirectories(entryPath);

                } else {
                    final Path dir = entryPath.getParent();
                    logger.debug("entry is file, inflating {} to {}", entry, dir);
                    Files.createDirectories(dir);
                    Files.copy(tin, entryPath);
                }
            }
        }
    }

}

