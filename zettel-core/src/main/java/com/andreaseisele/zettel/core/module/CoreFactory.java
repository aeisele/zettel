package com.andreaseisele.zettel.core.module;

import com.andreaseisele.zettel.core.scraper.chrome.ChromeDriverManager;
import com.andreaseisele.zettel.core.scraper.chromium.ChromiumManager;
import com.andreaseisele.zettel.core.storage.StorageManager;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = CoreModule.class)
public interface CoreFactory {

    StorageManager storageManager();

    ChromeDriverManager chromeDriverManager();

    ChromiumManager chromiumManager();

}
