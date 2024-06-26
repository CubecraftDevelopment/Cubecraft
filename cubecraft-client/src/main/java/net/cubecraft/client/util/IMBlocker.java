package net.cubecraft.client.util;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.concurrent.atomic.AtomicBoolean;

//https://github.com/reserveword/IMBlocker/
//special thanks :D
public interface IMBlocker {
    Logger LOGGER = LogManager.getLogger("IMBlocker");
    User32 USER = User32.INSTANCE;
    AtomicBoolean STATE = new AtomicBoolean(true);

    static void enable() {
        if (getState()) {
            return;
        }

        WinDef.HWND hwnd = USER.GetForegroundWindow();
        WinNT.HANDLE himc = WAPIAccess.ImmGetContext(hwnd);
        if (himc == null) {
            himc = WAPIAccess.ImmCreateContext();
            WAPIAccess.ImmAssociateContext(hwnd, himc);
        }
        WAPIAccess.ImmReleaseContext(hwnd, himc);
        STATE.set(true);
    }

    static void disable() {
        if (!getState()) {
            return;
        }
        WinDef.HWND hwnd = USER.GetForegroundWindow();
        WinNT.HANDLE himc = WAPIAccess.ImmAssociateContext(hwnd, null);
        if (himc != null) {
            WAPIAccess.ImmDestroyContext(himc);
        }
        WAPIAccess.ImmReleaseContext(hwnd, himc);
        STATE.set(false);
    }

    static void set(boolean on) {
        if (getState() == on) {
            return;
        }
        STATE.set(on);
        WinDef.HWND hwnd = USER.GetForegroundWindow();
        WinNT.HANDLE himc;
        if (on) {
            himc = WAPIAccess.ImmGetContext(hwnd);
            if (himc == null) {
                himc = WAPIAccess.ImmCreateContext();
                WAPIAccess.ImmAssociateContext(hwnd, himc);
            }
        } else {
            himc = WAPIAccess.ImmAssociateContext(hwnd, null);
            if (himc != null) {
                WAPIAccess.ImmDestroyContext(himc);
            }
        }
        WAPIAccess.ImmReleaseContext(hwnd, himc);
    }

    static void sync() {
        WinDef.HWND hwnd = USER.GetForegroundWindow();
        WinNT.HANDLE himc = WAPIAccess.ImmGetContext(hwnd);
        if ((himc == null) == STATE.get()) {
            LOGGER.warn("IM state inconsistent! state={}, im={}", STATE, himc != null);
            toggle();
        }
    }

    static boolean getState() {
        return STATE.get();
    }

    @SuppressWarnings("UnusedReturnValue")
    static boolean toggle() {
        boolean result;
        WinDef.HWND hwnd = USER.GetForegroundWindow();
        WinNT.HANDLE himc = WAPIAccess.ImmGetContext(hwnd);
        if (himc == null) {
            himc = WAPIAccess.ImmCreateContext();
            WAPIAccess.ImmAssociateContext(hwnd, himc);
            WAPIAccess.ImmReleaseContext(hwnd, himc);
            result = true;
        } else {
            himc = WAPIAccess.ImmAssociateContext(hwnd, null);
            WAPIAccess.ImmDestroyContext(himc);
            WAPIAccess.ImmReleaseContext(hwnd, himc);
            result = false;
        }
        STATE.set(result);
        return STATE.get();
    }


    class WAPIAccess {
        static {
            Native.register("imm32");
        }

        private static native WinNT.HANDLE ImmGetContext(WinDef.HWND hwnd);

        private static native WinNT.HANDLE ImmAssociateContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

        private static native boolean ImmReleaseContext(WinDef.HWND hwnd, WinNT.HANDLE himc);

        private static native WinNT.HANDLE ImmCreateContext();

        private static native boolean ImmDestroyContext(WinNT.HANDLE himc);
    }
}
