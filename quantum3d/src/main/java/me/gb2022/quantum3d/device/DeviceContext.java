package me.gb2022.quantum3d.device;

/**
 * The DeviceContext interface represents the context for managing various
 * devices and resources in a Quantum3D application.
 */
public interface DeviceContext {

    /**
     * Retrieves the main application window associated with the context.
     *
     * @return The main application window.
     */
    Window window();

    /**
     * Creates and provides a Mouse instance associated with a specific window.
     *
     * @param window The window for which the Mouse instance is created.
     * @return A Mouse instance associated with the given window.
     */
    Mouse mouse(Window window);

    /**
     * Creates and provides a Keyboard instance associated with a specific window.
     *
     * @param window The window for which the Keyboard instance is created.
     * @return A Keyboard instance associated with the given window.
     */
    Keyboard keyboard(Window window);

    /**
     * Initializes the device context and its associated resources.
     * This method should be called before using any devices or resources.
     */
    void initContext();

    /**
     * Destroys the device context and releases associated resources.
     * This method should be called to clean up before the application exits.
     */
    void destroyContext();
}