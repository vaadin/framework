/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.shared.Version;

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
        options.addOption("f", "theme-folder", true,
                "the folder containing the theme");
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

        // Regular theme
        try {
            processSassTheme(themeFolder, themeName, "styles",
                    Version.getFullVersion());
            System.out.println("Compiling theme " + themeName
                    + " styles successful");
        } catch (Exception e) {
            System.err.println("Compiling theme " + themeName
                    + " styles failed");
            e.printStackTrace();
        }
        // Legacy theme w/o .themename{} wrapping
        try {
            processSassTheme(themeFolder, themeName, "legacy-styles",
                    Version.getFullVersion());
            System.out.println("Compiling theme " + themeName
                    + " legacy-styles successful");
        } catch (Exception e) {
            System.err.println("Compiling theme " + themeName
                    + " legacy-styles failed");
            e.printStackTrace();
        }
    }

    private static void processSassTheme(String themeFolder, String themeName,
            String variant, String version) throws Exception {

        StringBuffer cssHeader = new StringBuffer();

        String stylesCssDir = themeFolder + File.separator + themeName
                + File.separator;

        String stylesCssName = stylesCssDir + variant + ".css";

        // Process as SASS file
        String sassFile = stylesCssDir + variant + ".scss";

        ScssStylesheet scss = ScssStylesheet.get(sassFile);
        if (scss == null) {
            throw new IllegalArgumentException("SASS file: " + sassFile
                    + " not found");
        }
        scss.compile();
        BufferedWriter out = new BufferedWriter(new FileWriter(stylesCssName));
        out.write(cssHeader.toString());
        out.write(scss.toString().replace("@version@", version));
        out.close();

        System.out.println("Compiled CSS to " + stylesCssName + " ("
                + scss.toString().length() + " bytes)");

        createSprites(themeFolder, themeName);
        File oldCss = new File(stylesCssName);
        File newCss = new File(stylesCssDir + variant + "-sprite.css");

        if (newCss.exists()) {
            // Theme contained sprites. Renamed "styles-sprite.css" ->
            // "styles.css"
            oldCss.delete();

            boolean ok = newCss.renameTo(oldCss);
            if (!ok) {
                throw new RuntimeException("Rename " + newCss + " -> " + oldCss
                        + " failed");
            }
        }

    }

    private static void createSprites(String themeFolder, String themeName)
            throws FileNotFoundException, IOException {
        try {
            // Try loading the class separately from using it to avoid
            // hiding other classpath issues
            Class<?> smartSpritesClass = org.carrot2.labs.smartsprites.SmartSprites.class;
        } catch (NoClassDefFoundError e) {
            System.err
                    .println("Could not find smartsprites. No sprites were generated. The theme should still work.");
            return;
        }

        String[] parameters = new String[] { "--sprite-png-depth", "AUTO",
                "--css-file-suffix", "-sprite", "--css-file-encoding", "UTF-8",
                "--root-dir-path", themeFolder + File.separator + themeName,
                "--log-level", "WARN" };

        org.carrot2.labs.smartsprites.SmartSprites.main(parameters);
        System.out.println("Generated sprites");

    }
}
