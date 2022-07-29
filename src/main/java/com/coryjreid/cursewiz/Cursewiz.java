package com.coryjreid.cursewiz;

import java.io.IOException;

import com.coryjreid.cursewiz.util.PackwizUtil;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

public class Cursewiz {
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

        System.out.println(PackwizUtil.getAndExtractPackwizExecutable().getAbsolutePath());
    }

    private static void setupArgumentParser() throws JSAPException {
        sJsap.registerParameter(new FlaggedOption("curseforgeInstancePath")
            .setStringParser(JSAP.STRING_PARSER)
            .setRequired(true)
            .setShortFlag('c')
            .setLongFlag(JSAP.NO_LONGFLAG)
            .setHelp("Path to the CurseForge App-managed Minecraft instance directory"));

        sJsap.registerParameter(new FlaggedOption("modpackProjectPath")
            .setStringParser(JSAP.STRING_PARSER)
            .setRequired(true)
            .setShortFlag('m')
            .setLongFlag(JSAP.NO_LONGFLAG)
            .setHelp("Path to the modpack project directory"));
    }
}
