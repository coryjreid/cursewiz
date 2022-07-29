package com.coryjreid.cursewiz.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.coryjreid.cursewiz.Cursewiz;

public final class PackwizUtil {

    private static final String PACKWIZ = "packwiz";

    /**
     * Prevent instantiation of utility class.
     */
    private PackwizUtil() {
        // Nothing to do
    }

    public static File getAndExtractPackwizExecutable() throws IOException {
        final File packwizFile = File.createTempFile(
            SystemUtil.getTemporaryDirectory() + "packwiz",
            getPackwizExecutableSuffix());
        packwizFile.deleteOnExit();

        try (
            final InputStream inputStream = getFileFromResourceAsStream(PackwizUtil.getPackwizResourceName());
            final FileOutputStream outputStream = new FileOutputStream(packwizFile)) {
            outputStream.write(inputStream.readAllBytes());
        }
        return packwizFile;
    }

    private static String getPackwizResourceName() {
        return PACKWIZ + "/x86_64/"
            + SystemUtil.getOS().toString().toLowerCase()
            + "/" + getPackwizExecutableName();
    }

    private static String getPackwizExecutableName() {
        return PACKWIZ + getPackwizExecutableSuffix();
    }

    private static String getPackwizExecutableSuffix() {
        return SystemUtil.getOS() == SystemUtil.OS.WINDOWS ? ".exe" : "";
    }

    private static InputStream getFileFromResourceAsStream(final String fileName) {
        final InputStream inputStream = Cursewiz.class.getClassLoader().getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }

    }
}
