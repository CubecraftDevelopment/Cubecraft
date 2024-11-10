package net.cubecraft.client.util;

public enum Platform {
    WINDOWS_X86("windows", "x86"),
    WINDOWS_X64("windows", "x64"),
    WINDOWS_ARM64("windows", "arm64"),
    WINDOWS_ARM32("windows", "arm32"),

    MACOS_X64("macos", "x64"),
    MACOS_ARM64("macos", "arm64"),

    LINUX_X64("linux", "x64"),
    LINUX_X86("linux", "x86"),
    LINUX_ARM32("linux", "arm32"),
    LINUX_ARM64("linux", "arm64");

    private final String name;
    private final String arch;

    Platform(String name, String arch) {
        this.name = name;
        this.arch = arch;
    }

    public static Platform current() {
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        boolean is64Bit = osArch.contains("64") || osArch.startsWith("armv8");
        boolean isArm = osArch.startsWith("arm") || osArch.startsWith("aarch64");

        if (osName.startsWith("Windows")) {
            if (isArm) {
                if (is64Bit) {
                    return WINDOWS_ARM64;
                }
                return WINDOWS_ARM32;
            } else {
                if (is64Bit) {
                    return WINDOWS_X64;
                }
                return WINDOWS_X86;
            }
        } else if (osName.startsWith("Linux") || osName.startsWith("FreeBSD") || osName.startsWith("SunOS") || osName.startsWith("Unix")) {
            if (isArm) {
                if (is64Bit) {
                    return LINUX_ARM64;
                }
                return LINUX_ARM32;
            } else {
                if (is64Bit) {
                    return LINUX_X64;
                }
                return LINUX_X86;
            }
        } else if (osName.startsWith("Mac OS X") || osName.startsWith("Darwin")) {
            if (isArm) {
                return MACOS_ARM64;
            } else {
                return MACOS_X64;
            }
        } else {
            throw new LinkageError("Unknown platform: " + osName);
        }
    }

    public static boolean is64Bit(Platform platform) {
        return switch (platform) {
            case LINUX_X64, LINUX_ARM64, WINDOWS_ARM64, WINDOWS_X64, MACOS_ARM64, MACOS_X64 -> true;
            default -> false;
        };
    }

    public static boolean isArm(Platform platform) {
        return switch (platform) {
            case LINUX_ARM32, LINUX_ARM64, WINDOWS_ARM32, WINDOWS_ARM64, MACOS_ARM64 -> true;
            default -> false;
        };
    }

    public String getName() {
        return name;
    }

    public String getArch() {
        return arch;
    }

    @Override
    public String toString() {
        return "%s-%s".formatted(this.getName(),this.getArch());
    }
}
