package com.vaadin.buildhelpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import com.vaadin.sass.ScssStylesheet;

/**
 * Helper to combine css divided into separate per component dirs into one to
 * optimize http requests.
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

        for (String themeName : new String[] { BASE, RUNO, LIFERAY, CHAMELEON }) {
            try {
                processSassTheme(themeName, false, ver);
                System.out.println("Compiling theme " + themeName
                        + " successful");
            } catch (Exception e) {
                System.err.println("Compiling theme " + themeName + " failed");
                e.printStackTrace();
            }
        }

        // Compile Reindeer last, since it requires the spriting operation
        // (makes testing the other themes a bit faster, since you don't need to
        // wait for the spriting operation to finish before the theme CSS is
        // compiled)
        for (String themeName : new String[] { REINDEER }) {
            try {
                processSassTheme(themeName, true, ver);
                System.out.println("Compiling theme " + themeName
                        + " successful");
            } catch (Exception e) {
                System.err.println("Compiling theme " + themeName + " failed");
                e.printStackTrace();
            }
        }
    }

    private static void processSassTheme(String themeName,
            boolean useSmartSprites, String version) throws Exception {

        StringBuffer cssHeader = new StringBuffer();

        // Theme version
        if (version == null) {
            version = "9.9.9.INTERNAL-DEBUG-BUILD";
        }
        version = version.replaceAll("\\.", "_");
        cssHeader.append(".v-theme-version:after {content:\"" + version
                + "\";}\n");
        cssHeader.append(".v-theme-version-" + version + " {display: none;}\n");

        String stylesCssDir = THEME_DIR + themeName + "/";
        String stylesCssName = stylesCssDir + "styles.css";

        // Process as SASS file
        File inputFile = new File(stylesCssDir + "styles.scss");
        ScssStylesheet scss = ScssStylesheet.get(inputFile);
        scss.compile();

        BufferedWriter out = new BufferedWriter(new FileWriter(stylesCssName));
        out.write(cssHeader.toString());
        out.write(scss.toString());
        out.close();

        System.out.println("Compiled CSS to " + stylesCssName + " ("
                + scss.toString().length() + " bytes)");

        if (useSmartSprites) {
            createSprites(themeName);
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
                "--css-file-suffix", "-sprite", "--css-file-encoding", "UTF-8",
                "--root-dir-path", THEME_DIR + themeName, "--log-level", "WARN" };

        org.carrot2.labs.smartsprites.SmartSprites.main(parameters);

    }
}
