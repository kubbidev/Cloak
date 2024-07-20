package me.kubbidev.cloak.util;

import com.mojang.authlib.GameProfile;
import me.kubbidev.cloak.CloakClient;
import me.kubbidev.cloak.model.CloakType;
import me.kubbidev.cloak.config.CloakConfiguration;
import me.kubbidev.cloak.config.ConfigKeys;
import me.kubbidev.cloak.model.User;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApiStatus.Internal
@ApiStatus.Experimental
public final class CloakProcessor {
    // create a threadpool to process the users concurrently
    private static final ExecutorService capeExecutor = Executors.newFixedThreadPool(2);

    private CloakProcessor() {
    }

    public static void loadTextures(CloakClient client, GameProfile profile) {
        User user = client.getUserManager().getOrMake(profile);

        if (profile.equals(MinecraftClient.getInstance().getGameProfile())) {
            CloakConfiguration configuration = client.getConfiguration();
            user.setCloak(false);

            capeExecutor.submit(() -> {
                user.setCloakType(configuration.get(ConfigKeys.CLIENT_CLOAK_TYPE));
            });
        } else {
            capeExecutor.submit(() -> {
                for (CloakType type : CloakType.values()) {
                    if (user.setCloakType(type)) break;
                }
            });
        }
    }

    public static void getCapeTexture(CloakClient client, GameProfile profile, CallbackInfoReturnable<SkinTextures> callback) {
        User user = client.getUserManager().getOrMake(profile);
        if (user.isCloak()) {
            CloakConfiguration configuration = client.getConfiguration();
            SkinTextures oldTextures = callback.getReturnValue();

            Identifier cloakTexture = user.getCloakIdentifier();
            Identifier elytraTexture = user.isElytraTexture() && configuration.get(ConfigKeys.CLIENT_ELYTRA_ENABLED)
                    ? cloakTexture
                    : Identifier.of("textures/entity/elytra.png");

            SkinTextures newTextures = new SkinTextures(
                    oldTextures.texture(),
                    oldTextures.textureUrl(),
                    cloakTexture,
                    elytraTexture,
                    oldTextures.model(),
                    oldTextures.secure()
            );
            callback.setReturnValue(newTextures);
        }
    }
}
