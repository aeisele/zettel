package com.andreaseisele.zettel.cli;

import com.andreaseisele.zettel.core.module.CoreFactory;
import com.andreaseisele.zettel.core.module.DaggerCoreFactory;
import com.andreaseisele.zettel.core.scraper.chrome.ChromeDriverManager;
import com.andreaseisele.zettel.core.scraper.chromium.ChromiumManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        CoreFactory coreFactory = DaggerCoreFactory.create();

        final ChromeDriverManager chromeDriverManager = coreFactory.chromeDriverManager();
        final ChromiumManager chromiumManager = coreFactory.chromiumManager();

        try {
            chromeDriverManager.installDriver();
            chromiumManager.installChromium();
            System.out.println("chromium binary " + chromiumManager.getBinary());
        } catch (IOException e) {
            logger.error("exception in main", e);
        }
    }

}
