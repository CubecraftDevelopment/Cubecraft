package net.cubecraft.util;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GraphicsCard;

public interface SystemInfoQuery {
    SystemInfo SYSTEM_INFO = new SystemInfo();
    CentralProcessor PROCESSOR = SYSTEM_INFO.getHardware().getProcessor();
    GraphicsCard GRAPHICS_CARD = SYSTEM_INFO.getHardware().getGraphicsCards()
            .get(SYSTEM_INFO.getHardware().getGraphicsCards().size()-1);

    static void update() {

    }

    static String getCPUInfo() {
        return "%s@%sGHZ".formatted(
                PROCESSOR.getProcessorIdentifier().getName(),
                PROCESSOR.getCurrentFreq()[0] / 1024 / 1024 / 1024f
        );
    }

    static String getGPUInfo() {
        return "%s@%sG".formatted(
                GRAPHICS_CARD.getName(),
                GRAPHICS_CARD.getVRam() / 1024 / 1024 / 1024
        );
    }

    static int getCPULoad() {
        return 0;
    }
}
