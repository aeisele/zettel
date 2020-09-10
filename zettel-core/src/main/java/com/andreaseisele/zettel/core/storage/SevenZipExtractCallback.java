package com.andreaseisele.zettel.core.storage;

import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

public class SevenZipExtractCallback implements IArchiveExtractCallback {

    private static final Logger logger = LoggerFactory.getLogger(SevenZipExtractCallback.class);

    private final IInArchive archive;

    private final Path target;

    private int currentIndex;

    private RandomAccessFile currentFile;

    public SevenZipExtractCallback(IInArchive archive, Path target) {
        this.archive = archive;
        this.target = target;
    }

    @Override
    public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) throws SevenZipException {

        this.currentIndex = index;

        if (extractAskMode != ExtractAskMode.EXTRACT) {
            return null;
        }

        final String entryName = archive.getStringProperty(index, PropID.PATH);

        final Path entryPath = target.resolve(entryName);

        try {
            if (isDirectory()) {
                logger.debug("entry {} is directory, creating {}", entryName, entryPath);
                Files.createDirectories(entryPath);
                return null;
            }

            final Path dir = entryPath.getParent();
            logger.debug("entry is file, inflating {} to {}", entryName, dir);
            Files.createDirectories(dir);
            this.currentFile = new RandomAccessFile(entryPath.toFile(), "rw");
            return new RandomAccessFileOutStream(currentFile);

        } catch (IOException e) {
            if (currentFile != null) {
                try {
                    currentFile.close();
                } catch (IOException ioException) {
                    logger.error("error closing current file after exception", ioException);
                }
            }
            throw new SevenZipException("error extracting index " + index, e);
        }
    }

    @Override
    public void prepareOperation(ExtractAskMode extractAskMode) throws SevenZipException {

    }

    @Override
    public void setOperationResult(ExtractOperationResult extractOperationResult) throws SevenZipException {
        if (isDirectory()) {
            return;
        }

        if (currentFile != null) {
            try {
                currentFile.close();
                currentFile = null;
            } catch (IOException e) {
                logger.error("error closing file " + currentFile + " after completion: " + e.getMessage());
                throw new SevenZipException("error closing file after completion", e);
            }
        }

        if (extractOperationResult != ExtractOperationResult.OK) {
            logger.error("7z extraction error");
            throw new SevenZipException("extraction operation result not ok");
        }
    }

    @Override
    public void setTotal(long total) throws SevenZipException {
        logger.debug("extract total {}", total);
    }

    @Override
    public void setCompleted(long complete) throws SevenZipException {
        logger.debug("extract completed {}", complete);
    }

    private boolean isDirectory() throws SevenZipException {
        return (boolean) archive.getProperty(currentIndex, PropID.IS_FOLDER);
    }

}
