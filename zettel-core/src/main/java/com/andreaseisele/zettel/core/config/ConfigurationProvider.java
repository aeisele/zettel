package com.andreaseisele.zettel.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Singleton
public class ConfigurationProvider {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationProvider.class);

    private static final String CORE_CONFIG_RESOURCE = "/core.properties";

    private final Properties config;

    @Inject
    public ConfigurationProvider() {
        this.config = new Properties();
        try (InputStream in = getClass().getResourceAsStream(CORE_CONFIG_RESOURCE)) {
            config.load(in);
        } catch (IOException e) {
            String message = "error loading core configuration from properties: " + e.getMessage();
            logger.error(message);
            throw new ConfigurationException(message, e);
        }
    }

    public String getValue(String key) {
        return config.getProperty(key);
    }

}
