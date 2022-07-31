package com.coryjreid.cursewiz;

import java.io.IOException;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

public class Cursewiz {
    static final String CURSEFORGE_INSTANCE_PATH_FLAG = "curseforgeInstancePath";
    static final String MODPACK_PROJECT_PATH_FLAG = "modpackProjectPath";
    static final String MODPACK_PROJECT_VERSION_FLAG = "modpackVersion";

    private static final JSAP sJsap = new JSAP();

    public static void main(final String[] args) throws JSAPException, IOException {
        setupArgumentParser();

        final JSAPResult parsedArgs = sJsap.parse(args);
        if (!parsedArgs.success()) {
            System.err.println();
            System.err.println("Usage: java " + Cursewiz.class.getName() + " " + sJsap.getUsage());
            System.err.println();
            System.err.println(sJsap.getHelp());
            System.err.println();
            System.exit(1);
        }

        if (parsedArgs.contains(MODPACK_PROJECT_VERSION_FLAG)) {
            Migrator.doMigration(
                parsedArgs.getString(CURSEFORGE_INSTANCE_PATH_FLAG),
                parsedArgs.getString(MODPACK_PROJECT_PATH_FLAG),
                parsedArgs.getString(MODPACK_PROJECT_VERSION_FLAG));
        } else {
            Migrator.doMigration(
                parsedArgs.getString(CURSEFORGE_INSTANCE_PATH_FLAG),
                parsedArgs.getString(MODPACK_PROJECT_PATH_FLAG));
        }
    }

    private static void setupArgumentParser() throws JSAPException {
        sJsap.registerParameter(new FlaggedOption(CURSEFORGE_INSTANCE_PATH_FLAG)
            .setStringParser(JSAP.STRING_PARSER)
            .setRequired(true)
            .setShortFlag('c')
            .setLongFlag(JSAP.NO_LONGFLAG)
            .setHelp("Path to the CurseForge App-managed Minecraft instance directory"));

        sJsap.registerParameter(new FlaggedOption(MODPACK_PROJECT_PATH_FLAG)
            .setStringParser(JSAP.STRING_PARSER)
            .setRequired(true)
            .setShortFlag('m')
            .setLongFlag(JSAP.NO_LONGFLAG)
            .setHelp("Path to the modpack project directory"));

        sJsap.registerParameter(new FlaggedOption(MODPACK_PROJECT_VERSION_FLAG)
            .setStringParser(JSAP.STRING_PARSER)
            .setRequired(false)
            .setShortFlag('v')
            .setLongFlag(JSAP.NO_LONGFLAG)
            .setHelp("Modpack version to set for BetterCompatabilityChecker & VERSION file"));
    }
}
