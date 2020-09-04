package com.andreaseisele.zettel.core.scraper;

import org.openqa.selenium.WebDriver;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public interface Scraper {

    public List<Path> download(WebDriver driver, LocalDate start, LocalDate end);
}
