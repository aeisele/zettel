package com.andreaseisele.zettel.core.scraper;

import com.andreaseisele.zettel.core.credential.CredentialStore;
import com.andreaseisele.zettel.core.credential.data.UsernamePasswordCredential;
import com.andreaseisele.zettel.core.credential.simple.SimpleCredentialStore;
import com.andreaseisele.zettel.core.module.CoreFactory;
import com.andreaseisele.zettel.core.module.DaggerCoreFactory;
import com.andreaseisele.zettel.core.scraper.chromium.ChromiumManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;


public class ScraperTest {

    private static final String ENV_TEST_MASTER_PW = "ZETTEL_TEST_MASTER_PW";
    private static final String CREDENTIAL_STORE = "testStore.dat";
    private static final String KEY_HETZNER = "hetzner";

    private ChromiumManager chromiumManager;

    private CredentialStore credentialStore;

    private ChromeDriver chromeDriver;

    @BeforeEach
    void setUp() throws IOException, URISyntaxException {
        final CoreFactory coreFactory = DaggerCoreFactory.create();

        setupChromium(coreFactory);
        setupCredentialStore();
    }

    private void setupChromium(CoreFactory coreFactory) throws IOException {
        chromiumManager = coreFactory.chromiumManager();
        chromiumManager.installChromium();
        chromiumManager.setupChromeDriver();


        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setBinary(chromiumManager.findMainBinary().toAbsolutePath().toString());
        // todo headless mode once stuff works
        this.chromeDriver = new ChromeDriver(chromeOptions);
        chromeDriver.manage().timeouts().implicitlyWait(2, TimeUnit.MINUTES);
    }

    @AfterEach
    void tearDown() {
        if (chromeDriver != null) {
            chromeDriver.close();
            chromeDriver.quit();
        }
    }

    private void setupCredentialStore() throws URISyntaxException {
        String masterPw = System.getenv(ENV_TEST_MASTER_PW);
        assertThat(masterPw).as("credential store master pw ENV variable").isNotNull();

        Path store = Paths.get(getClass().getResource(CREDENTIAL_STORE).toURI());
        this.credentialStore = SimpleCredentialStore.loadFromFile(store, masterPw.toCharArray());
    }

    @Test
    public void mvp() throws IOException {
        final LocalDate beginning = LocalDate.of(2020, 1, 1);
        final LocalDate end = LocalDate.of(2020, 1, 31);


        final Optional<UsernamePasswordCredential> credential = credentialStore.get(KEY_HETZNER, UsernamePasswordCredential.class);
        assertThat(credential).isPresent();

        // was ist der proper test hierfuer?
        final HetznerSeleniumScraper scraper = new HetznerSeleniumScraper(credential.get());
        final List<Path> belege = scraper.download(chromeDriver, beginning, end);
        assertThat(belege).hasSize(5);
    }
}
