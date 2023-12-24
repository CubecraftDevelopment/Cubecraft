package me.gb2022.quantum3d.device;

/**
 * The Device interface represents a platform-specific device, such as a graphics device, used in the Quantum3D platform.
 */
public interface Device {

    /**
     * Creates and initializes the device.
     * This method should be called to set up the device before any rendering or interaction takes place.
     */
    void create();

    /**
     * Destroys the device and releases any associated resources.
     * This method should be called to clean up the device when it's no longer needed.
     */
    void destroy();

    /**
     * Performs any necessary updates or processing related to the device.
     * This method can be overridden by implementing classes to provide custom update logic.
     * The default implementation does nothing.
     */
    default void update() {
        // Default implementation does nothing.
    }
}