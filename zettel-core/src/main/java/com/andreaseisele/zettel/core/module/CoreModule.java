package com.andreaseisele.zettel.core.module;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

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
        return new OkHttpClient();
    }

}
