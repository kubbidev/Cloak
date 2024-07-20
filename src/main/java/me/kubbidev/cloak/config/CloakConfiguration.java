package me.kubbidev.cloak.config;

import lombok.Getter;
import me.kubbidev.cloak.CloakClient;
import me.kubbidev.cloak.config.generic.KeyedConfiguration;
import me.kubbidev.cloak.config.generic.adapter.ConfigurationAdapter;

@Getter
public class CloakConfiguration extends KeyedConfiguration {
    private final CloakClient client;

    public CloakConfiguration(CloakClient client, ConfigurationAdapter adapter) {
        super(adapter, ConfigKeys.getKeys());
        this.client = client;

        init();
    }

    @Override
    public void reload() {
        super.reload();
    }
}
