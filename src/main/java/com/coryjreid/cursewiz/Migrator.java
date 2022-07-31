package com.coryjreid.cursewiz;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.coryjreid.cursewiz.json.InstalledAddon;
import com.coryjreid.cursewiz.json.MinecraftInstance;
import com.coryjreid.cursewiz.toml.PackMod;
import com.coryjreid.cursewiz.util.PackwizUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.moandjiezana.toml.Toml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Migrator {
    private static final Logger sLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final Set<String> sFolders = new HashSet<>();

    static {
        sFolders.add("config");
        sFolders.add("defaultconfigs");
        sFolders.add("kubejs");
        sFolders.add("resourcepacks");
        sFolders.add("scripts");
    }

    public static void doMigration(final String curseInstancePath, final String modpackProjectPath) throws IOException {
        doPackwizChanges(curseInstancePath, modpackProjectPath);
        doConfigCopy(curseInstancePath, modpackProjectPath);
    }

    private static void doConfigCopy(final String curseInstancePath, final String modpackProjectPath)
        throws IOException {

        // Delete the files from modpack
        for (final String folder : sFolders) {
            final Path pathToDelete = Paths.get(modpackProjectPath, folder);
            sLogger.info(String.format("Deleting path %s", pathToDelete));
            try (final Stream<Path> stream = Files.walk(pathToDelete)) {
                stream.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            }
        }

        // Copy files to modpack
        for (final String folder : sFolders) {
            final Path sourceDirectory = Paths.get(curseInstancePath, folder);
            final Path destinationDirectory = Paths.get(modpackProjectPath, folder);
            try (final Stream<Path> stream = Files.walk(sourceDirectory)) {
                stream.forEach(sourcePath -> {
                    try {
                        Path targetPath = destinationDirectory.resolve(sourceDirectory.relativize(sourcePath));
                        sLogger.info(String.format("Copying %s to %s", sourcePath, targetPath));
                        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (final IOException exception) {
                        sLogger.error("Failed to copy", exception);
                    }
                });
            }
        }
    }

    private static void doPackwizChanges(final String curseInstancePath, final String modpackProjectPath)
        throws IOException {
        final File packwizFile = PackwizUtil.getAndExtractPackwizExecutable();
        final File minecraftInstanceFile = new File(curseInstancePath + File.separator + "minecraftinstance.json");
        final MinecraftInstance minecraftInstance =
            new ObjectMapper().readValue(minecraftInstanceFile, MinecraftInstance.class);

        final Map<Long, String> packModSlugMap;
        final Map<Long, PackMod> packModMap;
        try (final Stream<Path> stream = Files.walk(Path.of(modpackProjectPath))) {
            packModSlugMap = getPackModSlugMapFromStreamTomlFiles(stream);
        }
        try (final Stream<Path> stream = Files.walk(Path.of(modpackProjectPath))) {
            packModMap = getPackModMapFromStreamTomlFiles(stream);
        }

        for (final InstalledAddon instanceMod : minecraftInstance.getInstalledAddons()) {
            final PackMod packMod = packModMap.get(instanceMod.getAddonId());
            if (packMod == null) {
                // pack install
                sLogger.info(String.format(
                    "Installing new mod ID %s file ID %s",
                    instanceMod.getAddonId(),
                    instanceMod.getFileId()));
                new ProcessBuilder()
                    .inheritIO()
                    .directory(new File(modpackProjectPath))
                    .command(
                        packwizFile.getAbsolutePath(),
                        "curseforge",
                        "install",
                        "--addon-id",
                        String.valueOf(instanceMod.getAddonId()),
                        "--file-id",
                        String.valueOf(instanceMod.getFileId()))
                    .start();
            } else if (packMod.getFileId() != instanceMod.getFileId()) {
                // pack update
                sLogger.info(String.format(
                    "Updating mod ID %s file ID %s",
                    instanceMod.getAddonId(),
                    instanceMod.getFileId()));
                new ProcessBuilder()
                    .inheritIO()
                    .directory(new File(modpackProjectPath))
                    .command(
                        packwizFile.getAbsolutePath(),
                        "curseforge",
                        "install",
                        "--addon-id",
                        String.valueOf(instanceMod.getAddonId()),
                        "--file-id",
                        String.valueOf(instanceMod.getFileId()))
                    .start();
            }
        }

        // Check for things to delete
        // "packMods - instanceMods = mods in packMods that do not exist in instanceMods"
        final Set<Long> toBeDeleted = Sets.difference(
            packModMap.keySet(),
            minecraftInstance.getInstalledAddons()
                .stream()
                .map(InstalledAddon::getAddonId)
                .collect(Collectors.toSet()));
        for (final Long id : toBeDeleted) {
            // delete mod from pack
            sLogger.info(String.format("Deleting mod ID %s", id));
            new ProcessBuilder()
                .inheritIO()
                .directory(new File(modpackProjectPath))
                .command(
                    packwizFile.getAbsolutePath(),
                    "remove",
                    packModSlugMap.get(id))
                .start();
        }
    }

    private static Map<Long, String> getPackModSlugMapFromStreamTomlFiles(final Stream<Path> stream) {
        final Map<Long, String> map = new HashMap<>();
        for (final Path path : stream.collect(Collectors.toList())) {
            if (path.toString().contains("optifine") || Files.isDirectory(path) || !path.toString().contains("mods")) {
                continue;
            }

            final PackMod packMod = PackMod.createPackModFromToml(new Toml().read(path.toFile()));
            final String fileName = path.getFileName().toString();
            final String slug = fileName.substring(0, fileName.indexOf('.'));

            map.put(packMod.getProjectId(), slug);
        }

        return map;
    }

    private static Map<Long, PackMod> getPackModMapFromStreamTomlFiles(final Stream<Path> stream) {
        final Map<Long, PackMod> modMap = new HashMap<>();
        final Stream<Path> modTomlFiles = stream.filter(p -> p.toString().contains("mods") && Files.isRegularFile(p));
        modTomlFiles
            .filter(p -> !p.toString().contains("optifine")) // Remove optifine
            .map(p -> PackMod.createPackModFromToml(new Toml().read(p.toFile()))) // Map to PackMod object
            .forEach(packMod -> modMap.put(packMod.getProjectId(), packMod));
        return modMap;
    }
}
