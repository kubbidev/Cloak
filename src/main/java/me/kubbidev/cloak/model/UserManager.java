package me.kubbidev.cloak.model;

import com.mojang.authlib.GameProfile;
import me.kubbidev.cloak.CloakClient;
import me.kubbidev.cloak.model.manager.AbstractManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class UserManager extends AbstractManager<UUID, User, User> {
    private final CloakClient client;

    public UserManager(CloakClient client) {
        this.client = client;
    }

    @NotNull
    public User getOrMake(@NotNull GameProfile profile) {
        return getOrMake(profile.getId(), profile.getName());
    }

    @NotNull
    public User getOrMake(@NotNull UUID uuid, @Nullable String username) {
        User user = getOrMake(uuid);
        if (username != null) {
            user.setUsername(username, false);
        }
        return user;
    }

    @Override
    public User apply(UUID uuid) {
        return new User(uuid, this.client);
    }
}
