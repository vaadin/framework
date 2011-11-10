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

    private static final String ARG_VERSION = "-version";

    private static final String THEME_DIR = "./WebContent/VAADIN/themes/";
    private static final String BASE = "base";
    private static final String RUNO = "runo";
    private static final String REINDEER = "reindeer";
    private static final String LIFERAY = "liferay";
    private static final String CHAMELEON = "chameleon";

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String ver = null;
        for (int i = 0; i < args.length; i++) {
            if (ARG_VERSION.equals(args[i])) {
                if (args.length >= i) {
                    ver = args[i + 1];
                }
                break;
            }
        }
        // Compile Reindeer last, since it requires the spriting operation
        // (makes testing the other themes a bit faster, since you don't need to
        // wait for the spriting operation to finish before the theme CSS is
        // compiled)
        combineTheme(new String[] { BASE }, false, ver);
        combineTheme(new String[] { BASE, RUNO }, false, ver);
        combineTheme(new String[] { BASE, LIFERAY }, false, ver);
        combineTheme(new String[] { BASE, CHAMELEON }, false, ver);
        combineTheme(new String[] { BASE, REINDEER }, true, ver);
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
            boolean useSmartSprites, String version) throws IOException {

        StringBuffer combinedCss = new StringBuffer();

        // Theme version
        if (version == null) {
            version = "9.9.9.INTERNAL-DEBUG-BUILD";
        }
        version = version.replaceAll("\\.", "_");
        combinedCss.append(".v-theme-version:after {content:\"" + version
                + "\";}\n");
        combinedCss.append(".v-theme-version-" + version
                + " {display: none;}\n");

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
                    // or
                    // @import "subdir1[/subdir2]*/file-to-import.css"
                    // ".." and other similar paths are not allowed in the url
                    String importFilename = strLine.split("\"")[1];

                    File importFile = new File(THEME_DIR + themeName + "/"
                            + folder + "/" + importFilename);
                    if (importFile.isFile()) {
                        String currentFolder = folder;
                        if (importFilename.contains("/")) {
                            currentFolder = currentFolder
                                    + "/"
                                    + importFilename.substring(0,
                                            importFilename.lastIndexOf("/"));
                        }
                        processCSSFile(importFile, currentFolder, themeName,
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

                strLine = updateUrls(folder, themeName, inheritedFile, strLine);

                if (!strLine.startsWith("@import")) {
                    combinedCss.append(strLine);
                    combinedCss.append("\n");
                }
            }
            // Close the input stream
            in.close();
        }
    }

    private static String updateUrls(String folder, String themeName,
            boolean inheritedFile, String strLine) {
        // Define image url prefix
        String urlPrefix = "";
        if (inheritedFile) {
            urlPrefix = "../" + themeName + "/";
        }

        if (strLine.indexOf("url(/") > 0) {
            // Do nothing for urls beginning with /
        } else if (strLine.indexOf("url(../") >= 0) {
            // eliminate a path segment in the folder name for every
            // "../"
            String[] folderSegments = folder.split("/");
            int segmentCount = folderSegments.length;
            while (segmentCount > 0 && strLine.indexOf("url(../") >= 0) {
                segmentCount--;
                strLine = strLine.replaceAll("url\\(../", ("url\\("));
            }
            // add remaining path segments to urlPrefix
            StringBuilder sb = new StringBuilder(urlPrefix);
            for (int i = 0; i < segmentCount; i++) {
                sb.append(folderSegments[i]);
                sb.append("/");
            }
            strLine = strLine.replaceAll("url\\(", ("url\\(" + sb.toString()));

        } else {
            strLine = strLine.replaceAll("url\\(", ("url\\(" + urlPrefix
                    + folder + "/"));

        }
        return strLine;
    }

    private static void createSprites(String themeName)
            throws FileNotFoundException, IOException {
        String[] parameters = new String[] { "--sprite-png-depth", "AUTO",
                "--css-file-suffix", "-sprite", "--css-file-encoding", "UTF-8",
                "--root-dir-path", THEME_DIR + themeName, "--log-level", "WARN" };

        org.carrot2.labs.smartsprites.SmartSprites.main(parameters);

    }
}
