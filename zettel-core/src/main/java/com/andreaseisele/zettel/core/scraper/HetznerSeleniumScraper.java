package com.andreaseisele.zettel.core.scraper;

import com.andreaseisele.zettel.core.credential.data.UsernamePasswordCredential;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.nio.CharBuffer;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class HetznerSeleniumScraper implements Scraper {


    private final UsernamePasswordCredential credential;

    public HetznerSeleniumScraper(UsernamePasswordCredential credential) {
        this.credential = credential;
    }

    public List<Path> download(WebDriver driver, LocalDate start, LocalDate end) {

        driver.get("https://accounts.hetzner.com/login");

        // ####### 1 Login
        // TODO put in credentials from credentials store
        driver.findElement(By.id("_username")).sendKeys(CharBuffer.wrap(credential.getUsername()));
        driver.findElement(By.id("_password")).sendKeys(CharBuffer.wrap(credential.getPassword()));
        driver.findElement(By.id("submit-login")).click();


        // ####### 2 Open Invoices Page
        driver.get("https://accounts.hetzner.com/invoice");


        // ####### 3 Download Correct Invoices

        // TODO Downloaden bedeutet, man setzt zich properties um beim chrome einen download ohne "nachfragen" zu starten.

        // ...in ein verzeichnis dass man vorher am chrome setzen muss

        // ...um nach dem download die downgeloadeten dateien aus dem verzeichnis zu scannen...

        return List.of();
    }


}