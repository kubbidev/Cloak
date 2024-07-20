package me.kubbidev.cloak.model;

import me.kubbidev.cloak.config.CloakConfiguration;
import me.kubbidev.cloak.config.ConfigKeys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("SpellCheckingInspection")
public enum CloakType {

    MINECRAFT,
    OPTIFINE;

    @Nullable
    public String getUrl(@NotNull User user) {
        CloakConfiguration configuration = user.getClient().getConfiguration();
        if (this == CloakType.OPTIFINE && configuration.get(ConfigKeys.CLIENT_OPTIFINE_ENABLED)) {
            return "http://s.optifine.net/capes/" + user.getPlainDisplayName() + ".png";
        }
        return null;
    }
}
