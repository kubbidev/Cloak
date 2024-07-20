package me.kubbidev.cloak.mixins;

import com.mojang.authlib.GameProfile;
import me.kubbidev.cloak.CloakClientProvider;
import me.kubbidev.cloak.util.CloakProcessor;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(PlayerListEntry.class)
public class MixinPlayerListEntry {

    @Shadow
    @Final
    private GameProfile profile;

    @Inject(method = "texturesSupplier", at = @At("HEAD"))
    private static void loadTextures(GameProfile profile, CallbackInfoReturnable<Supplier<SkinTextures>> callback) {
        CloakProcessor.loadTextures(CloakClientProvider.get(), profile);
    }

    @Inject(method = "getSkinTextures", at = @At("TAIL"), cancellable = true)
    private void getCapeTexture(CallbackInfoReturnable<SkinTextures> callback) {
        CloakProcessor.getCapeTexture(CloakClientProvider.get(), this.profile, callback);
    }
}