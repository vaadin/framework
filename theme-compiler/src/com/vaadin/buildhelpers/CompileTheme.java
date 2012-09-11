package com.vaadin.buildhelpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.vaadin.sass.ScssStylesheet;

/**
 * Helper to combine css divided into separate per component dirs into one to
 * optimize http requests.
 */
public class CompileTheme {

    /**
     * @param args
     * @throws IOException
     * @throws ParseException
     */
    public static void main(String[] args) throws IOException, ParseException {
        Options options = new Options();
        options.addOption("t", "theme", true, "the theme to compile");
        options.addOption("v", "theme-version", true,
                "the version to add to the compiled theme");
        options.addOption("f", "theme-folder", true,
                "the folder containing the theme");
        options.addOption("s", "sprites", false, "use smartsprites");
        CommandLineParser parser = new PosixParser();
        CommandLine params = parser.parse(options, args);
        if (!params.hasOption("theme") || !params.hasOption("theme-folder")) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(CompileTheme.class.getName(), options);
            return;
        }
        String themeName = params.getOptionValue("theme");
        String themeFolder = params.getOptionValue("theme-folder");
        String themeVersion = params.getOptionValue("theme-version");
        boolean useSprites = params.hasOption("sprites");

        try {
            processSassTheme(themeFolder, themeName, useSprites, themeVersion);
            System.out.println("Compiling theme " + themeName + " successful");
        } catch (Exception e) {
            System.err.println("Compiling theme " + themeName + " failed");
            e.printStackTrace();
        }
    }

    private static void processSassTheme(String themeFolder, String themeName,
            boolean useSmartSprites, String version) throws Exception {

        StringBuffer cssHeader = new StringBuffer();

        version = version.replaceAll("\\.", "_");
        cssHeader.append(".v-theme-version:after {content:\"" + version
                + "\";}\n");
        cssHeader.append(".v-theme-version-" + version + " {display: none;}\n");

        String stylesCssDir = themeFolder + File.separator + themeName
                + File.separator;
        String stylesCssName = stylesCssDir + "styles.css";

        // Process as SASS file
        String sassFile = stylesCssDir + "styles.scss";
        ScssStylesheet scss = ScssStylesheet.get(sassFile);
        if (scss == null) {
            throw new IllegalArgumentException("SASS file: " + sassFile
                    + " not found");
        }
        scss.compile();

        BufferedWriter out = new BufferedWriter(new FileWriter(stylesCssName));
        out.write(cssHeader.toString());
        out.write(scss.toString());
        out.close();

        System.out.println("Compiled CSS to " + stylesCssName + " ("
                + scss.toString().length() + " bytes)");

        if (useSmartSprites) {
            createSprites(themeFolder, themeName);
            System.out.println("Used SmartSprites to create sprites");
            File oldCss = new File(stylesCssName);
            oldCss.delete();

            File newCss = new File(stylesCssDir + "styles-sprite.css");
            boolean ok = newCss.renameTo(oldCss);
            if (!ok) {
                System.out.println("Rename " + newCss + " -> " + oldCss
                        + " failed");
            }
        }
    }

    private static void createSprites(String themeFolder, String themeName)
            throws FileNotFoundException, IOException {
        String[] parameters = new String[] { "--sprite-png-depth", "AUTO",
                "--css-file-suffix", "-sprite", "--css-file-encoding", "UTF-8",
                "--root-dir-path", themeFolder + File.separator + themeName,
                "--log-level", "WARN" };

        org.carrot2.labs.smartsprites.SmartSprites.main(parameters);

    }
}
