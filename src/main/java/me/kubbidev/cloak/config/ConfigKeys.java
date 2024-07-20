package me.kubbidev.cloak.config;

import me.kubbidev.cloak.model.CloakType;
import me.kubbidev.cloak.config.generic.KeyedConfiguration;
import me.kubbidev.cloak.config.generic.key.ConfigKey;
import me.kubbidev.cloak.config.generic.key.SimpleConfigKey;

import java.util.List;
import java.util.Locale;

import static me.kubbidev.cloak.config.generic.key.ConfigKeyFactory.*;

/**
 * All of the {@link ConfigKey}s used by BlockTune.
 *
 * <p>The {@link #getKeys()} method and associated behaviour allows this class
 * to function a bit like an enum, but with generics.</p>
 */
public final class ConfigKeys {
    private ConfigKeys() {
    }

    /**
     * Which type of cloak we should display when rendering cloaks.
     */
    public static final ConfigKey<CloakType> CLIENT_CLOAK_TYPE = key(c -> {
        try {
            return CloakType.valueOf(c.getString("client-cloak-type", "MINECRAFT"));
        } catch (Exception e) {
            return CloakType.MINECRAFT;
        }
    });

    /**
     * If Cloak should also display cloaks from Optifine.
     */
    public static final ConfigKey<Boolean> CLIENT_OPTIFINE_ENABLED = booleanKey("client-optifine-enabled", true);

    /**
     * If Cloak should also display cloaks texture on elytra.
     */
    public static final ConfigKey<Boolean> CLIENT_ELYTRA_ENABLED = booleanKey("client-elytra-enabled", true);

    /**
     * A list of the keys defined in this class.
     */
    private static final List<SimpleConfigKey<?>> KEYS = KeyedConfiguration.initialise(ConfigKeys.class);

    public static List<? extends ConfigKey<?>> getKeys() {
        return KEYS;
    }

    /**
     * Check if the value at the given path should be censored in console/log output
     *
     * @param path the path
     * @return true if the value should be censored
     */
    public static boolean shouldCensorValue(final String path) {
        final String lower = path.toLowerCase(Locale.ROOT);
        return lower.contains("password") || lower.contains("uri");
    }
}