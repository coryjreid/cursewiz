package com.coryjreid.cursewiz.util;

public final class SystemUtil {
    private static OS sOperatingSystem = null;
    private static final String sTemporaryDirectory = System.getProperty("java.io.tmpdir");

    /**
     * Prevent instantiation of utility class.
     */
    private SystemUtil() {
        // Nothing to do
    }

    public static OS getOS() {
        if (sOperatingSystem == null) {
            final String operatingSystem = System.getProperty("os.name").toLowerCase();
            if (operatingSystem.contains("win")) {
                sOperatingSystem = OS.WINDOWS;
            } else if (operatingSystem.contains("nix") || operatingSystem.contains("nux") || operatingSystem.contains(
                "aix")) {
                sOperatingSystem = OS.LINUX;
            } else if (operatingSystem.contains("mac")) {
                sOperatingSystem = OS.MACOS;
            } else if (operatingSystem.contains("sunos")) {
                sOperatingSystem = OS.SOLARIS;
            }
        }
        return sOperatingSystem;
    }

    public static String getTemporaryDirectory() {
        return sTemporaryDirectory;
    }

    public enum OS {
        WINDOWS,
        LINUX,
        MACOS,
        SOLARIS
    }
}
