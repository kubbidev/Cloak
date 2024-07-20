package me.kubbidev.cloak.config.generic.adapter;

import me.kubbidev.cloak.CloakClient;
import me.kubbidev.cloak.config.ConfigKeys;
import org.jetbrains.annotations.Nullable;

public class SystemPropertyConfigAdapter extends StringBasedConfigurationAdapter {
    private static final String PREFIX = "blocktune.";

    private final CloakClient client;

    public SystemPropertyConfigAdapter(CloakClient client) {
        this.client = client;
    }

    @Override
    protected @Nullable String resolveValue(String path) {
        // e.g.
        // 'server'            -> blocktune.server
        // 'data.table_prefix' -> blocktune.data.table-prefix
        String key = PREFIX + path;

        String value = System.getProperty(key);
        if (value != null) {
            String printableValue = ConfigKeys.shouldCensorValue(path) ? "*****" : value;
            this.client.getLogger().info(String.format("Resolved configuration value from system property: %s = %s", key, printableValue));
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