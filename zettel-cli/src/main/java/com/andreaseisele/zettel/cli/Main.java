package com.andreaseisele.zettel.cli;

import com.andreaseisele.zettel.core.module.CoreFactory;
import com.andreaseisele.zettel.core.module.DaggerCoreFactory;
import com.andreaseisele.zettel.core.scraper.chromium.ChromiumManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        CoreFactory coreFactory = DaggerCoreFactory.create();

        final ChromiumManager chromiumManager = coreFactory.chromiumManager();

        try {
            chromiumManager.installChromium();
            System.out.println("chromium binary " + chromiumManager.findMainBinary());
        } catch (IOException e) {
            logger.error("exception in main", e);
        }
    }

}
