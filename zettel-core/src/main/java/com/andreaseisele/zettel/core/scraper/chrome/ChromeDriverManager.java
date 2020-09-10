package com.andreaseisele.zettel.core.scraper.chrome;

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
public class ChromeDriverManager {

    private static final Logger logger = LoggerFactory.getLogger(ChromeDriverManager.class);

    private static final String DRIVER_URL = "https://chromedriver.storage.googleapis.com/";
    private static final String DRIVER_ZIP_PREFIX = "chromedriver_";
    private static final String DRIVER_ZIP_EXT = ".zip";
    private static final String DRIVER_ZIP_WIN = "win32";
    private static final String DRIVER_ZIP_LINUX = "linux64";
    private static final String DRIVER_ZIP_MAC = "mac64";
    private static final String KEY_DRIVER_VERSION = "chrome-driver.version";
    private static final String KEY_DRIVER_BINARY_PREFIX = "chrome-driver.binary.";
    private static final String DRIVER_DIR = "chromeDriver";
    private static final String ENV_DRIVER_PATH = "webdriver.chrome.driver";

    private final StorageManager storageManager;
    private final ConfigurationProvider configurationProvider;
    private final Downloader downloader;

    @Inject
    public ChromeDriverManager(StorageManager storageManager,
                               ConfigurationProvider configurationProvider,
                               Downloader downloader) {
        this.storageManager = storageManager;
        this.configurationProvider = configurationProvider;
        this.downloader = downloader;
    }

    public void installDriver() throws IOException {
        String driverVersion = configurationProvider.getValue(KEY_DRIVER_VERSION);
        if (driverVersion == null) {
            throw new ChromeDriverException("unable to determine driver version from config");
        }

        Path driverDir = storageManager.getApplicationSubDirectory(DRIVER_DIR);
        Path versionDir = driverDir.resolve(driverVersion);
        if (Files.exists(versionDir)) {
            logger.info("driver dir {} does exist, not installing driver", versionDir);
        } else {
            logger.info("installing driver {} into {}", driverVersion, versionDir);
            downloadAndUnzipDriver(driverVersion, versionDir);
        }

        setupEnvPropertyForDir(versionDir);
    }

    public String getBinaryName() {
        final String binaryName = configurationProvider.getValue(KEY_DRIVER_BINARY_PREFIX
                + OperatingSystem.getCurrent());
        if (binaryName == null) {
            throw new ChromeDriverException("unable to determine binary name from config");
        }
        return binaryName;
    }

    public void setupEnvPropertyForBinary(Path binary) {
        final String absoluteDir = binary.toAbsolutePath().toString();
        logger.debug("setting up env variable {} to point to {}", ENV_DRIVER_PATH, absoluteDir);
        System.setProperty(ENV_DRIVER_PATH, absoluteDir);
    }

    private void downloadAndUnzipDriver(String driverVersion, Path versionDir) throws IOException {
        Files.createDirectories(versionDir);
        final String driverZipName = constructDriverZipName();
        final Path driverZip = versionDir.resolve(driverZipName);
        final boolean downloadSuccess = downloader.download(
                new URL(DRIVER_URL + driverVersion + "/" + driverZipName),
                driverZip);
        if (!downloadSuccess) {
            throw new ChromeDriverException("download of chrome driver failed");
        }
        Zipper.unzip(driverZip, versionDir);
    }

    private String constructDriverZipName() {
        StringBuilder driverZipBuilder = new StringBuilder(DRIVER_ZIP_PREFIX);
        switch (OperatingSystem.getCurrent()) {
            case WINDOWS:
                driverZipBuilder.append(DRIVER_ZIP_WIN);
                break;
            case MAC:
                driverZipBuilder.append(DRIVER_ZIP_MAC);
                break;
            case LINUX:
                driverZipBuilder.append(DRIVER_ZIP_LINUX);
                break;
            default:
                throw new RuntimeException("unsupported operating system " + OperatingSystem.getCurrent());
        }
        return driverZipBuilder.append(DRIVER_ZIP_EXT).toString();
    }

    private void setupEnvPropertyForDir(Path dir) {
        final String binaryName = getBinaryName();
        setupEnvPropertyForBinary(dir.resolve(binaryName));
    }

}
