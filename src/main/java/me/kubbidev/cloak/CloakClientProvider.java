package me.kubbidev.cloak;

import org.jetbrains.annotations.ApiStatus;

/**
 * Provides static access to the {@link Cloak} API.
 *
 * <p>Ideally, the ServiceManager for the platform should be used to obtain an
 * instance, however, this provider can be used if this is not viable.</p>
 */
public final class CloakClientProvider {
    private static CloakClient instance = null;

    /**
     * Gets an instance of the {@link Cloak} API,
     * throwing {@link IllegalStateException} if the API is not loaded yet.
     *
     * <p>This method will never return null.</p>
     *
     * @return an instance of the Cloak API
     * @throws IllegalStateException if the API is not loaded yet
     */
    public static CloakClient get() {
        CloakClient instance = CloakClientProvider.instance;
        if (instance == null) {
            throw new NotLoadedException();
        }
        return instance;
    }

    @ApiStatus.Internal
    static void register(CloakClient instance) {
        CloakClientProvider.instance = instance;
    }

    @ApiStatus.Internal
    static void unregister() {
        CloakClientProvider.instance = null;
    }

    @ApiStatus.Internal
    private CloakClientProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Exception thrown when the API is requested before it has been loaded.
     */
    private static final class NotLoadedException extends IllegalStateException {
        private static final String MESSAGE = """
                The Cloak API isn't loaded yet!
                This could be because:
                  a) the Cloak mod is not installed or it failed to enable
                  b) the mod in the stacktrace does not declare a dependency on Cloak
                  c) the mod in the stacktrace is retrieving the API before the mod 'enable' phase
                     (call the #get method in onEnable, not the constructor!)
                """;

        NotLoadedException() {
            super(MESSAGE);
        }
    }
}