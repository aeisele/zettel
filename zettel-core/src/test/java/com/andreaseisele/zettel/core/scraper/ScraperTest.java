package com.andreaseisele.zettel.core.scraper;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class ScraperTest {

    @Test
    public void mvp() throws IOException {
        // Für jeden Browser braucht Selenium einen "Driver" zum fernsteuern, in der richtigen Version.
        // TODO unpack locally and set right reference
        System.setProperty("webdriver.chrome.driver", "C:\\dev\\zettel\\zettel-core\\src\\test\\resources\\chrome\\driver\\chromedriver.exe");

        ChromeOptions chromeOptions = new ChromeOptions();
        // Und den Chrome selber...
        // todo unpack locally and reference
        chromeOptions.setBinary("C:\\dev\\zettel\\zettel-core\\src\\test\\resources\\chrome\\App\\Chrome-bin\\chrome.exe");
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
