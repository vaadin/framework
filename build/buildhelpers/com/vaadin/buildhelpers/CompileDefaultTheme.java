package com.vaadin.buildhelpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Helper to combine css divded into separate per component dirs into one to
 * optimize http requests.
 * 
 */
public class CompileDefaultTheme {

    private static final String THEME_DIR = "./WebContent/VAADIN/themes/";
    private static final String BASE = "base";
    private static final String RUNO = "runo";
    private static final String REINDEER = "reindeer";

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        combineTheme(new String[] { BASE }, false);
        combineTheme(new String[] { BASE, RUNO }, false);
        combineTheme(new String[] { BASE, REINDEER }, true);
    }

    /**
     * 
     * @param themeNames
     *            All themes that should be combined together (to include
     *            inheritance). The order is the same in which the styles are
     *            catenated. The resulted file is placed in the last specified
     *            theme folder.
     * 
     * @param
     * @throws IOException
     */
    private static void combineTheme(String[] themeNames,
            boolean useSmartSprites) throws IOException {

        StringBuffer combinedCss = new StringBuffer();

        for (int j = 0; j < themeNames.length; j++) {
            File f = new File(THEME_DIR + themeNames[j]);
            combinedCss
                    .append("/* Automatically compiled css file from subdirectories. */\n");

            File[] subdir = f.listFiles();
            Arrays.sort(subdir, new Comparator<File>() {
                public int compare(File arg0, File arg1) {
                    return arg0.compareTo(arg1);
                }
            });

            for (int i = 0; i < subdir.length; i++) {
                File dir = subdir[i];
                String folder = dir.getName();
                String filename = dir.getPath() + "/" + folder + ".css";

                processCSSFile(new File(filename), folder, themeNames[j],
                        combinedCss, j < themeNames.length - 1);
            }
        }

        String stylesCssDir = THEME_DIR + themeNames[themeNames.length - 1]
                + "/";
        String stylesCssName = stylesCssDir + "styles.css";

        BufferedWriter out = new BufferedWriter(new FileWriter(stylesCssName));
        out.write(combinedCss.toString());
        out.close();

        System.out.println("Compiled CSS to " + THEME_DIR
                + themeNames[themeNames.length - 1] + "/styles.css ("
                + combinedCss.toString().length() + " bytes)");

        if (useSmartSprites) {
            createSprites(themeNames[themeNames.length - 1]);
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

    private static void processCSSFile(File cssFile, String folder,
            String themeName, StringBuffer combinedCss, boolean inheritedFile)
            throws FileNotFoundException, IOException {
        if (cssFile.isFile()) {

            combinedCss.append("\n");

            FileInputStream fstream = new FileInputStream(cssFile);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {

                // Parse import rules
                if (strLine.startsWith("@import")) {
                    // All import statements must be exactly
                    // @import "file-to-import.css";
                    // No sub-directories are allowed in the url
                    String importFilename = strLine.split("\"")[1];

                    File importFile = new File(THEME_DIR + themeName + "/"
                            + folder + "/" + importFilename);
                    if (importFile.isFile()) {
                        processCSSFile(importFile, folder, themeName,
                                combinedCss, inheritedFile);
                    } else {
                        System.out
                                .println("File not found for @import statement "
                                        + THEME_DIR
                                        + themeName
                                        + "/"
                                        + folder
                                        + "/" + importFilename);
                    }
                }

                // Define image url prefix
                String urlPrefix = "";
                if (inheritedFile) {
                    urlPrefix = "../" + themeName + "/";
                }

                if (strLine.indexOf("url(../") > 0) {
                    strLine = strLine.replaceAll("url\\(../",
                            ("url\\(" + urlPrefix));

                } else {
                    strLine = strLine.replaceAll("url\\(", ("url\\("
                            + urlPrefix + folder + "/"));

                }
                if (!strLine.startsWith("@import")) {
                    combinedCss.append(strLine);
                    combinedCss.append("\n");
                }
            }
            // Close the input stream
            in.close();
        }
    }

    private static void createSprites(String themeName)
            throws FileNotFoundException, IOException {
        String[] parameters = new String[] { "--sprite-png-depth", "AUTO",
                "--sprite-png-ie6", "--css-file-suffix", "-sprite",
                "--css-file-encoding", "UTF-8", "--root-dir-path",
                THEME_DIR + themeName, "--log-level", "WARN" };

        org.carrot2.labs.smartsprites.SmartSprites.main(parameters);

    }
}
