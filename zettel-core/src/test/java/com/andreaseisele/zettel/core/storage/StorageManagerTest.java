package com.andreaseisele.zettel.core.storage;

import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;


class StorageManagerTest {

    private FileSystem fs;

    @BeforeEach
    void setUp() {
        this.fs = Jimfs.newFileSystem();
    }

    @AfterEach
    void tearDown() throws IOException {
        fs.close();
    }

    @Test
    void testGetApplicationDirectory() throws IOException {
        Path applicationDirectory = new StorageManager(fs).getApplicationDirectory();
        assertThat(applicationDirectory).isNotNull();
    }

}