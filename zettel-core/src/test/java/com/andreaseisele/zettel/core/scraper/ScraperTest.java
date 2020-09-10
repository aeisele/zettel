package com.andreaseisele.zettel.core.scraper;

import com.andreaseisele.zettel.core.module.CoreFactory;
import com.andreaseisele.zettel.core.module.DaggerCoreFactory;
import com.andreaseisele.zettel.core.scraper.chrome.ChromeDriverManager;
import com.andreaseisele.zettel.core.scraper.chromium.ChromiumManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;


public class ScraperTest {

    private ChromeDriverManager chromeDriverManager;
    private ChromiumManager chromiumManager;

    @BeforeEach
    void setUp() throws IOException {
        final CoreFactory coreFactory = DaggerCoreFactory.create();

        chromiumManager = coreFactory.chromiumManager();
        chromiumManager.installChromium();

        chromeDriverManager = coreFactory.chromeDriverManager();
        chromeDriverManager.setupEnvPropertyForBinary(chromiumManager.findBinary(chromeDriverManager.getBinaryName()));
    }

    @Test
    public void mvp() throws IOException {
        // Für jeden Browser braucht Selenium einen "Driver" zum fernsteuern, in der richtigen Version.


        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setBinary(chromiumManager.findMainBinary().toAbsolutePath().toString());

        // todo headless mode once stuff works
        final ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);
        chromeDriver.manage().timeouts().implicitlyWait(2, TimeUnit.MINUTES);

        final LocalDate beginning = LocalDate.of(2020, 1, 1);
        final LocalDate end = LocalDate.of(2020, 1, 31);

        // was ist der proper test hierfuer?

        final List<Path> belege = new HetznerSeleniumScraper().download(chromeDriver, beginning, end);
        assertThat(belege).hasSize(5);
    }
}
