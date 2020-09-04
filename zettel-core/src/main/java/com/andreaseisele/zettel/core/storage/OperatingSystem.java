package com.andreaseisele.zettel.core.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum OperatingSystem {

    WINDOWS,
    MAC,
    LINUX;

    private static final Logger logger = LoggerFactory.getLogger(OperatingSystem.class);

    private static final OperatingSystem current;

    static {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            current = WINDOWS;
        } else if (osName.contains("mac")) {
            current = MAC;
        } else if (osName.contains("nux")) {
            current = LINUX;
        } else {
            throw new RuntimeException("unable to determine current operating system from " + osName);
        }

        logger.debug("determined current operating system to be: {}", current);
    }

    public static OperatingSystem getCurrent() {
        return current;
    }

}
