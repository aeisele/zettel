package com.andreaseisele.zettel.core.module;

import com.andreaseisele.zettel.core.http.LoggingProgressListener;
import com.andreaseisele.zettel.core.http.ProgressResponseBody;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import javax.inject.Singleton;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Module
public abstract class CoreModule {

    @Singleton
    @Provides
    static ExecutorService scraperThreadPool() {
        return Executors.newFixedThreadPool(10);
    }

    @Singleton
    @Provides
    static FileSystem fileSystem() {
        return FileSystems.getDefault();
    }

    @Singleton
    @Provides
    static OkHttpClient httpClient() {
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(chain -> {
                    Response originalResponse = chain.proceed(chain.request());
                    // todo: add way to customize what kind of listener is used
                    final LoggingProgressListener progressListener = new LoggingProgressListener();
                    return originalResponse.newBuilder()
                            .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                            .build();
                })
                .build();
    }

}
