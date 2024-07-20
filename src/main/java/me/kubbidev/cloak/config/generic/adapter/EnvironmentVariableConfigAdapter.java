package me.kubbidev.cloak.config.generic.adapter;

import me.kubbidev.cloak.CloakClient;
import me.kubbidev.cloak.config.ConfigKeys;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class EnvironmentVariableConfigAdapter extends StringBasedConfigurationAdapter {
    private static final String PREFIX = "CLOAK_";

    private final CloakClient client;

    public EnvironmentVariableConfigAdapter(CloakClient client) {
        this.client = client;
    }

    @Override
    protected @Nullable String resolveValue(String path) {
        // e.g.
        // 'server'            -> CLOAK_SERVER
        // 'data.table_prefix' -> CLOAK_DATA_TABLE_PREFIX
        String key = PREFIX + path.toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace('.', '_');

        String value = System.getenv(key);
        if (value != null) {
            String printableValue = ConfigKeys.shouldCensorValue(path) ? "*****" : value;
            this.client.getLogger().info(String.format("Resolved configuration value from environment variable: %s = %s", key, printableValue));
        }
        return value;
    }

    @Override
    public CloakClient getClient() {
        return this.client;
    }

    @Override
    public void reload() {
        // no-op
    }
}