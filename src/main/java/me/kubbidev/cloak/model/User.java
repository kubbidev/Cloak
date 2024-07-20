package me.kubbidev.cloak.model;

import lombok.Getter;
import lombok.Setter;
import me.kubbidev.cloak.CloakClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.*;
import java.util.Optional;
import java.util.UUID;

public class User {

    /**
     * Reference to the main client instance
     *
     * @see #getClient()
     */
    @Getter
    private final CloakClient client;

    /**
     * The users Mojang UUID
     */
    @Getter
    private final UUID uniqueId;

    /**
     * The last known username of a player
     */
    private @Nullable String username = null;

    @Setter
    @Getter
    private boolean cloak = false;

    @Getter
    private boolean elytraTexture = true;

    @Getter
    private CloakType cloakType = null;

    public User(UUID uniqueId, CloakClient client) {
        this.client = client;
        this.uniqueId = uniqueId;
    }

    public Optional<String> getUsername() {
        return Optional.ofNullable(this.username);
    }

    public Text getFormattedDisplayName() {
        return Text.literal(getPlainDisplayName());
    }

    public String getPlainDisplayName() {
        return this.username != null ? this.username : this.uniqueId.toString();
    }

    /**
     * Sets the users name
     *
     * @param name the name to set
     * @param weak if true, the value will only be updated if a value hasn't been set previously.
     * @return true if a change was made
     */
    public boolean setUsername(String name, boolean weak) {
        if (name != null && name.length() > 16) {
            return false; // nope
        }

        // if weak is true, only update the value in the User if it's null
        if (weak && this.username != null) {

            // try to update casing if they're equalIgnoreCase
            if (this.username.equalsIgnoreCase(name)) {
                this.username = name;
            }

            return false;
        }

        // consistency. if the name being set is equivalent to null, just make it null.
        if (name != null && (name.isEmpty() || name.equalsIgnoreCase("null"))) {
            name = null;
        }

        // if one or the other is null, just update and return true
        if ((this.username == null) != (name == null)) {
            this.username = name;
            return true;
        }

        if (this.username == null) {
            // they're both null
            return false;
        } else {
            // both non-null
            if (this.username.equalsIgnoreCase(name)) {
                this.username = name; // update case anyway, but return false
                return false;
            } else {
                this.username = name;
                return true;
            }
        }
    }

    private @NotNull HttpURLConnection connection(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL()
                .openConnection(MinecraftClient.getInstance().getNetworkProxy());

        connection.addRequestProperty("User-Agent", "Mozilla/4.0");
        connection.setDoInput(true);
        connection.setDoOutput(false);

        return connection;

    }

    public @NotNull Identifier getCloakIdentifier() {
        return Identifier.of(this.uniqueId.toString());
    }

    public boolean setCloakType(@NotNull CloakType type) {
        boolean result = false;
        try {
            HttpURLConnection connection = connection(type.getUrl(this));
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode / 100 == 2) {
                result = setCloakTexture(connection.getInputStream());
            }
            if (result) {
                this.cloakType = type;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean setCloakTexture(@NotNull InputStream in) {
        try {
            NativeImage cloakImage = NativeImage.read(in);
            // now that we have the final cloak texture image, run the
            // next steps on the main minecraft client thread
            MinecraftClient.getInstance().submit(() -> {
                this.cloak = true;
                this.elytraTexture = Math.floorDiv(
                        cloakImage.getWidth(),
                        cloakImage.getHeight()) == 2;

                MinecraftClient.getInstance().getTextureManager().registerTexture(getCloakIdentifier(),
                        new NativeImageBackedTexture(parseCloak(cloakImage))
                );
            });
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private @NotNull NativeImage parseCloak(@NotNull NativeImage image) {
        int imageWidth = 64;
        int imageHeight = 32;
        int sourceWidth = image.getWidth();
        int sourceHeight = image.getHeight();
        while (imageWidth < sourceWidth || imageHeight < sourceHeight) {
            imageWidth *= 2;
            imageHeight *= 2;
        }
        NativeImage newImage = new NativeImage(imageWidth, imageHeight, true);
        for (int x = 0; x < sourceWidth; x++) {
            for (int y = 0; y < sourceHeight; y++) {
                newImage.setColor(x, y, image.getColor(x, y));
            }
        }
        image.close();
        return newImage;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof User other)) {
            return false;
        }
        return this.uniqueId.equals(other.uniqueId);
    }

    @Override
    public int hashCode() {
        return this.uniqueId.hashCode();
    }

    @Override
    public String toString() {
        return "User(uuid=" + this.uniqueId + ")";
    }
}
