package me.kubbidev.cloak;

import lombok.Getter;
import me.kubbidev.cloak.config.CloakConfiguration;
import me.kubbidev.cloak.config.generic.adapter.*;
import me.kubbidev.cloak.logging.PluginLogger;
import me.kubbidev.cloak.logging.Slf4jPluginLogger;
import me.kubbidev.cloak.model.UserManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

@Getter
public final class CloakClient implements ClientModInitializer {
    private static final String MOD_ID = "cloak";

    /**
     * The mod container.
     */
    private final ModContainer modContainer;

    /**
     * The plugin logger
     */
    private final PluginLogger logger;

    // init during enable
    private CloakConfiguration configuration;

    /*
        manager used to save and store each player
        data containing the cloak textures used on runtime.
     */
    private UserManager userManager;

    /**
     * The time when the plugin was enabled
     */
    private Instant startTime;

    public CloakClient() {
        this.modContainer = FabricLoader.getInstance().getModContainer(MOD_ID)
                .orElseThrow(() -> new RuntimeException("Could not get the Cloak mod container."));
        this.logger = new Slf4jPluginLogger(LoggerFactory.getLogger(MOD_ID));
    }

    @Override
    public void onInitializeClient() {
        this.startTime = Instant.now();

        // load configuration
        getLogger().info("Loading configuration...");
        ConfigurationAdapter configFileAdapter = provideConfigurationAdapter();
        this.configuration = new CloakConfiguration(this, new MultiConfigurationAdapter(this,
                new SystemPropertyConfigAdapter(this),
                new EnvironmentVariableConfigAdapter(this),
                configFileAdapter
        ));

        // setup user manager
        setupManagers();

        // register with the Cloak API
        CloakClientProvider.register(this);

        Duration timeTaken = Duration.between(getStartTime(), Instant.now());
        getLogger().info("Successfully enabled. (took " + timeTaken.toMillis() + "ms)");
    }

    private ConfigurationAdapter provideConfigurationAdapter() {
        return new PropertiesConfigAdapter(this, resolveConfig("config.properties"));
    }

    private void setupManagers() {
        this.userManager = new UserManager(this);
    }

    private Path resolveConfig(String fileName) {
        Path configFile = getConfigDirectory().resolve(fileName);

        // if the config doesn't exist, create it based on the template in the resources dir
        if (!Files.exists(configFile)) {
            try {
                Files.createDirectories(configFile.getParent());
            } catch (IOException e) {
                // ignore
            }

            try (InputStream is = getResourceStream(fileName)) {
                Files.copy(is, configFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return configFile;
    }

    // provide information about the platform

    /**
     * Gets the plugins configuration directory
     *
     * @return the config directory
     */
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
    }

    /**
     * Gets a bundled resource file from the jar
     *
     * @param path the path of the file
     * @return the file as an input stream
     */
    @SuppressWarnings("deprecation")
    public InputStream getResourceStream(String path) {
        try {
            return Files.newInputStream(this.modContainer.getPath(path));
        } catch (IOException e) {
            return null;
        }
    }
}
