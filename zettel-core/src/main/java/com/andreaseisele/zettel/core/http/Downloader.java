package com.andreaseisele.zettel.core.http;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Singleton
public class Downloader {

    private final OkHttpClient httpClient;

    @Inject
    public Downloader(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public boolean download(URL url, Path target) throws IOException {
        boolean success = false;

        Request request = new Request.Builder().url(url).build();
        Response response = httpClient.newCall(request).execute();

        if (response.isSuccessful()) {
            try (ResponseBody body = response.body()) {
                if (body != null) {
                    try (final InputStream in = body.byteStream()) {
                        final long copied = Files.copy(in, target);
                        success = copied > 0;
                    }
                }
            }
        }

        return success;
    }

    public static String getFileName(URL url) {
        return new File(url.getPath()).getName();
    }

}
