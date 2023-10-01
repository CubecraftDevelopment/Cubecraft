package ink.flybird.cubecraft.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ink.flybird.cubecraft.client.gui.base.Popup;
import ink.flybird.cubecraft.client.gui.ScreenUtil;
import ink.flybird.cubecraft.SharedContext;
import ink.flybird.fcommon.NetworkUtil;
import ink.flybird.fcommon.I18nHelper;
import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.logging.SimpleLogger;

import java.io.IOException;


public class VersionCheck implements Runnable {

    public static final String CHECK_VERSION_URL = "https://api.github.com/repos/FlyBirdGameStudio/Cubecraft/releases/tags/Client";

    private final Logger logger = new SimpleLogger("VersionCheck");

    @Override
    public void run() {
        I18nHelper i18nHelper = SharedContext.I18N;
        try {
            String request = NetworkUtil.httpGet(CHECK_VERSION_URL);
            JsonObject obj = JsonParser.parseString(request).getAsJsonObject();
            String version = obj.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString();
            String updateAt = obj.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("updated_at").getAsString();
            String download = obj.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
            String info = obj.get("body").getAsString();

            version = version.replace(".jar", "");

            int id = compare(version, CubecraftClient.VERSION);
            if (id == UNKNOWN_REMOTE) {
                ScreenUtil.createPopup(
                        i18nHelper.get("version_check.unknown_remote.title"),
                        i18nHelper.get("version_check.unknown_remote.subtitle", version),
                        60, Popup.INFO
                );
            }
            if (id == UNKNOWN_CLIENT) {
                ScreenUtil.createPopup(
                        i18nHelper.get("version_check.unknown_client.title"),
                        i18nHelper.get("version_check.unknown_client.subtitle", CubecraftClient.VERSION),
                        60, Popup.WARNING
                );
            }
            if (id == UNEXPECTED_NEWER) {
                ScreenUtil.createPopup(
                        i18nHelper.get("version_check.unexpected_client.title"),
                        i18nHelper.get("version_check.unexpected_client.subtitle", CubecraftClient.VERSION),
                        60, Popup.WARNING
                );
            }
            if (id == LATEST) {
                ScreenUtil.createPopup(
                        i18nHelper.get("version_check.latest.title"),
                        i18nHelper.get("version_check.latest.subtitle", CubecraftClient.VERSION),
                        60, Popup.SUCCESS
                );
            }
            if (id == OLDER) {
                ScreenUtil.createPopup(
                        i18nHelper.get("version_check.latest.title"),
                        i18nHelper.get("version_check.latest.subtitle", CubecraftClient.VERSION, version),
                        60, Popup.INFO
                );
            }
        } catch (IOException e) {
            logger.exception(e);
            ScreenUtil.createPopup(
                    i18nHelper.get("version_check.exception.title"),
                    i18nHelper.get("version_check.exception.subtitle"),
                    60, Popup.ERROR
            );
        }
    }


    public static final int UNKNOWN_REMOTE = 404;
    public static final int UNKNOWN_CLIENT = 303;
    public static final int UNEXPECTED_NEWER = 1;
    public static final int LATEST = 0;
    public static final int OLDER = -1;

    public int compare(String version, String target) {
        String[] num = version.split("\\.");
        String[] current = target.split("\\.");

        int[] numI = new int[3];

        try {
            for (int i = 0; i < 3; i++) {
                numI[i] = Integer.parseInt(num[i]);
            }
        } catch (Exception e) {
            return UNKNOWN_REMOTE;
        }
        if (current.length != 3) {
            return UNKNOWN_CLIENT;
        } else if (num.length != 3) {
            return UNKNOWN_REMOTE;
        } else {
            return LATEST;
        }
    }

    public static void check() {
        new Thread(new VersionCheck(), "client_version_check").start();
    }
}
