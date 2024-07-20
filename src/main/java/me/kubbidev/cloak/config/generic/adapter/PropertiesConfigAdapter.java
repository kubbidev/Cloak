package me.kubbidev.cloak.config.generic.adapter;

import me.kubbidev.cloak.CloakClient;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class PropertiesConfigAdapter extends StringBasedConfigurationAdapter {
    private final CloakClient client;
    private final Path file;
    private Properties properties;

    public PropertiesConfigAdapter(CloakClient client, Path file) {
        this.client = client;
        this.file = file;
        reload();
    }

    @Override
    protected @Nullable String resolveValue(String path) {
        return this.properties.getProperty(path);
    }

    @Override
    public CloakClient getClient() {
        return this.client;
    }

    @Override
    public void reload() {
        try (InputStream in = Files.newInputStream(this.file)) {
            this.properties = new Properties();
            this.properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}