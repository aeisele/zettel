package com.andreaseisele.zettel.core.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingProgressListener implements ProgressListener {

    private static final Logger logger = LoggerFactory.getLogger(LoggingProgressListener.class);

    boolean firstUpdate = true;

    @Override
    public void update(long bytesRead, long contentLength, boolean done) {
        if (done) {
            logger.info("completed");
        } else {
            if (firstUpdate) {
                firstUpdate = false;
                if (contentLength == -1) {
                    logger.info("content-length: unknown");
                } else {
                    logger.info("content-length: {}", contentLength);
                }
            }

            logger.info("bytes read: {}", bytesRead);

            if (contentLength != -1) {
                logger.info("{}% done", (100 * bytesRead) / contentLength);
            }
        }
    }

}
