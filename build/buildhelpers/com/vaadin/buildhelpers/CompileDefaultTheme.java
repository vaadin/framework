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
            Arrays.sort(subdir, new Comparator() {
                public int compare(Object arg0, Object arg1) {
                    return ((File) arg0).compareTo((File) arg1);
                }
            });

            for (int i = 0; i < subdir.length; i++) {
                File dir = subdir[i];
                String name = dir.getName();
                String filename = dir.getPath() + "/" + name + ".css";

                File cssFile = new File(filename);
                if (cssFile.isFile()) {

                    combinedCss.append("\n");
                    combinedCss.append("/* " + filename.replaceAll("\\\\", "/")
                            + " */");
                    combinedCss.append("\n");

                    FileInputStream fstream = new FileInputStream(cssFile);
                    // Get the object of DataInputStream
                    DataInputStream in = new DataInputStream(fstream);
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(in));
                    String strLine;
                    while ((strLine = br.readLine()) != null) {
                        // Define image url prefix
                        String urlPrefix = "";
                        if (j < themeNames.length - 1) {
                            urlPrefix = "../" + themeNames[j] + "/";
                        }

                        if (strLine.indexOf("url(../") > 0) {
                            strLine = strLine.replaceAll("url\\(../",
                                    ("url\\(" + urlPrefix));

                        } else {
                            strLine = strLine.replaceAll("url\\(", ("url\\("
                                    + urlPrefix + name + "/"));

                        }
                        combinedCss.append(strLine);
                        combinedCss.append("\n");
                    }
                    // Close the input stream
                    in.close();
                }
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

    private static void createSprites(String themeName)
            throws FileNotFoundException, IOException {
        String[] parameters = new String[] { "--sprite-png-depth", "AUTO",
                "--sprite-png-ie6", "--css-file-suffix", "-sprite",
                "--css-file-encoding", "UTF-8", "--root-dir-path",
                THEME_DIR + themeName, "--log-level", "WARN" };

        org.carrot2.labs.smartsprites.SmartSprites.main(parameters);

    }
}
