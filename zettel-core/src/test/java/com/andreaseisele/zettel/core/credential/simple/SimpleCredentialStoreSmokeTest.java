package com.andreaseisele.zettel.core.credential.simple;

import com.andreaseisele.zettel.core.credential.data.UsernamePasswordCredential;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleCredentialStoreSmokeTest {

    private final char[] MASTER_PASSWORD = new char[]{'p', 'a', 's', 's', 'w', 'o', 'r', 'd'};
    private static final String TEST_KEY = "test_entry";
    private static final char[] TEST_USER = new char[]{'u', 's', 'e', 'r'};
    private static final char[] TEST_PW = new char[]{'p', 'w'};

    private FileSystem fs;

    @BeforeEach
    void setUp() {
        this.fs = Jimfs.newFileSystem(Configuration.unix());
    }

    @AfterEach
    void tearDown() throws IOException {
        fs.close();
    }

    @Test
    void testCreateNewAndSave() throws IOException {
        Path outputFile = openPath("create-and-save", "store.dat");

        SimpleCredentialStore credentialStore = SimpleCredentialStore.createNew(MASTER_PASSWORD);
        credentialStore.put(TEST_KEY, new UsernamePasswordCredential(TEST_USER, TEST_PW));

        credentialStore.saveToFile(outputFile);
    }

    @Test
    void testOpenAndRead() throws IOException {
        Path inputFile = stageFileResource("open-and-read", "store.dat", "store.dat");

        SimpleCredentialStore credentialStore = SimpleCredentialStore.loadFromFile(inputFile, MASTER_PASSWORD);

        Optional<UsernamePasswordCredential> maybeCredential = credentialStore.get(TEST_KEY, UsernamePasswordCredential.class);
        assertThat(maybeCredential).isPresent();

        UsernamePasswordCredential credential = maybeCredential.get();
        assertThat(credential.getUsername()).isEqualTo(TEST_USER);
        assertThat(credential.getPassword()).isEqualTo(TEST_PW);
    }

    @Test
    void testRoundtrip() throws IOException {
        Path file = openPath("roundtrip", "store.dat");

        SimpleCredentialStore credentialStore = SimpleCredentialStore.createNew(MASTER_PASSWORD);
        credentialStore.put(TEST_KEY, new UsernamePasswordCredential(TEST_USER, TEST_PW));

        credentialStore.saveToFile(file);

        SimpleCredentialStore loaded = SimpleCredentialStore.loadFromFile(file, MASTER_PASSWORD);

        Optional<UsernamePasswordCredential> maybeCredential = loaded.get(TEST_KEY, UsernamePasswordCredential.class);
        assertThat(maybeCredential).isPresent();

        UsernamePasswordCredential credential = maybeCredential.get();
        assertThat(credential.getUsername()).isEqualTo(TEST_USER);
        assertThat(credential.getPassword()).isEqualTo(TEST_PW);
    }

    private Path openPath(String parent, String fileName) throws IOException {
        //Path dir = Paths.get(parent);
        Path dir = fs.getPath(parent);
        Files.createDirectories(dir);
        return dir.resolve(fileName);
    }

    private Path stageFileResource(String parent, String fileName, String resourceName) throws IOException {
        Path path = openPath(parent, fileName);
        try (InputStream resourceStream = getClass().getResourceAsStream(resourceName)) {
            Files.copy(resourceStream, path);
        }
        return path;
    }

}