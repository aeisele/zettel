package com.andreaseisele.zettel.core.scraper.chromium;

import com.andreaseisele.zettel.core.config.ConfigurationProvider;
import com.andreaseisele.zettel.core.http.Downloader;
import com.andreaseisele.zettel.core.storage.OperatingSystem;
import com.andreaseisele.zettel.core.storage.StorageManager;
import com.andreaseisele.zettel.core.storage.Zipper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Singleton
public class ChromiumManager {

    private static final Logger logger = LoggerFactory.getLogger(ChromiumManager.class);

    private static final String CHROMIUM_DIR = "chromium";
    private static final String KEY_CHROMIUM_VERSION = "chromium.version";
    private static final String KEY_CHROMIUM_URL_PREFIX = "chromium.url.";
    private static final String KEY_CHROMIUM_BINARY_PREFIX = "chromium.binary.";

    private final StorageManager storageManager;
    private final ConfigurationProvider configurationProvider;
    private final Downloader downloader;

    private Path currentChromiumDir;

    @Inject
    public ChromiumManager(StorageManager storageManager,
                           ConfigurationProvider configurationProvider,
                           Downloader downloader) {
        this.storageManager = storageManager;
        this.configurationProvider = configurationProvider;
        this.downloader = downloader;
    }

    public void installChromium() throws IOException {
        final String chromiumVersion = configurationProvider.getValue(KEY_CHROMIUM_VERSION);
        if (chromiumVersion == null) {
            throw new ChromiumException("unable to determine chromium version from config");
        }

        Path chromiumDir = storageManager.getApplicationSubDirectory(CHROMIUM_DIR);
        Path versionDir = chromiumDir.resolve(chromiumVersion);
        if (Files.exists(versionDir)) {
            logger.info("chromium dir {} does exist, not installing chromium", chromiumDir);
        } else {
            logger.info("installing chromium {} into {}", chromiumVersion, versionDir);
            downloadAndUnzipChromium(chromiumVersion, versionDir);
        }

        this.currentChromiumDir = versionDir;
    }

    public Path getBinary() throws IOException {
        final String binaryName = configurationProvider.getValue(KEY_CHROMIUM_BINARY_PREFIX
                + OperatingSystem.getCurrent());
        if (binaryName == null) {
            throw new ChromiumException("unable to determine binary name from config");
        }

        if (currentChromiumDir == null) {
            throw new ChromiumException("no chromium installed");
        }

        return Files.find(currentChromiumDir, 10,
                (path, basicFileAttributes) -> !Files.isDirectory(path)
                        && path.getFileName().toString().equals(binaryName))
                .findFirst()
                .orElseThrow(() -> new ChromiumException("unable to find chromium binary " + binaryName + " in " + currentChromiumDir));
    }

    private void downloadAndUnzipChromium(String chromiumVersion, Path versionDir) throws IOException {
        Files.createDirectories(versionDir);
        final URL downloadUrl = new URL(
                configurationProvider.getValue(KEY_CHROMIUM_URL_PREFIX + OperatingSystem.getCurrent()));

        final String fileName = Downloader.getFileName(downloadUrl);
        final Path chromiumFile = versionDir.resolve(fileName);
        final boolean downloadSuccess = downloader.download(downloadUrl, chromiumFile);
        if (!downloadSuccess) {
            throw new ChromiumException("download of chromium failed");
        }

        if (fileName.endsWith(".tar.xz")) {
            Path tar = versionDir.resolve(fileName.replace("tar.xz", ".tar"));
            Zipper.unXzip(chromiumFile, tar);
            Zipper.extract(tar, versionDir);
        } else {
            Zipper.extract(chromiumFile, versionDir);
        }
    }

}
