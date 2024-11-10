package me.gb2022.quantum3d.device;



import me.gb2022.quantum3d.device.listener.WindowListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The abstract class Window represents a graphical window within the Quantum3D platform.
 * It provides methods to manage window event listeners and control window-related functionality.
 */
public abstract class Window implements Device {
    private AtomicLong frame=new AtomicLong(0);
    private final List<WindowListener> listeners = new ArrayList<>(32);

    /**
     * Add a WindowListener to receive window events from this window.
     *
     * @param listener The WindowListener to be added.
     */
    public final void addListener(WindowListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Remove a previously added WindowListener from receiving events.
     *
     * @param listener The WindowListener to be removed.
     */
    public final void removeListener(WindowListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Get a list of all registered WindowListeners for this window.
     *
     * @return A list of registered WindowListeners.
     */
    public final List<WindowListener> getListeners() {
        return this.listeners;
    }

    /**
     * Calculate the aspect ratio of the window.
     *
     * @return The aspect ratio of the window (width divided by height).
     */
    public final float getAspect() {
        return this.getWidth() / (float) this.getHeight();
    }

    /**
     * Check if vertical synchronization (vsync) is enabled for this window.
     *
     * @return True if vsync is enabled, otherwise false.
     */
    public abstract boolean isVsync();

    /**
     * Enable or disable vertical synchronization (vsync) for this window.
     *
     * @param vsync True to enable vsync, false to disable.
     */
    public abstract void setVsync(boolean vsync);

    /**
     * Get the current title of the window.
     *
     * @return The title of the window.
     */
    public abstract String getTitle();

    /**
     * Set the title of the window.
     *
     * @param title The new title for the window.
     */
    public abstract void setTitle(String title);

    /**
     * Check if the window is in fullscreen mode.
     *
     * @return True if the window is in fullscreen mode, otherwise false.
     */
    public abstract boolean isFullscreen();

    /**
     * Set the window to fullscreen mode or exit fullscreen mode.
     *
     * @param fullscreen True to set the window to fullscreen, false to exit fullscreen.
     */
    public abstract void setFullscreen(boolean fullscreen);

    /**
     * Set the window's icon using the provided input stream.
     *
     * @param in The input stream containing the window icon image.
     */
    public abstract void setIcon(InputStream in);

    /**
     * Set the size of the window.
     *
     * @param width  The new width of the window.
     * @param height The new height of the window.
     */
    public abstract void setSize(int width, int height);

    /**
     * Set the position of the window.
     *
     * @param x The new X-coordinate of the window position.
     * @param y The new Y-coordinate of the window position.
     */
    public abstract void setPos(int x, int y);

    /**
     * Get the width of the window.
     *
     * @return The width of the window.
     */
    public abstract int getWidth();

    /**
     * Get the height of the window.
     *
     * @return The height of the window.
     */
    public abstract int getHeight();

    /**
     * Get the X-coordinate of the window position.
     *
     * @return The X-coordinate of the window position.
     */
    public abstract int getX();

    /**
     * Get the Y-coordinate of the window position.
     *
     * @return The Y-coordinate of the window position.
     */
    public abstract int getY();

    /**
     * Check if the window is currently focused.
     *
     * @return True if the window is focused, otherwise false.
     */
    public abstract boolean isFocused();

    /**
     * Check if the window is currently iconified (minimized).
     *
     * @return True if the window is iconified, otherwise false.
     */
    public abstract boolean isIconified();

    /**
     * check if window is resizeable
     *
     * @return true if window resizeable, otherwise not
     */
    public abstract boolean isResizeable();

    /**
     * Set the resize ability of the window.
     *
     * @param resizeable true if window resizeable, otherwise not.
     */
    public abstract void setResizeable(boolean resizeable);

    /**
     * Check if a close request has been made for the window.
     *
     * @return True if a close request has been made, otherwise false.
     */
    public abstract boolean isCloseRequested();

    public long getFrame() {
        return frame.get();
    }

    public void incrementFrame() {
        this.frame.incrementAndGet();
    }
}
