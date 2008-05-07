package com.itmill.toolkit.buildhelpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Helper to combine css diveded into separate per component dirs into one to
 * optimize http requests.
 * 
 */
public class CompileDefaultTheme {

    private static final String SRCDIR = "./WebContent/ITMILL/themes/default";

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        File f = new File(SRCDIR);

        StringBuffer combinedCss = new StringBuffer();
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

            File cssFile = new File(dir.getPath() + "/" + name + ".css");
            if (cssFile.isFile()) {
                FileInputStream fstream = new FileInputStream(cssFile);
                // Get the object of DataInputStream
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    if (strLine.indexOf("url(../") > 0) {
                        strLine = strLine.replaceAll("url\\(../", ("url\\("));

                    } else {
                        strLine = strLine.replaceAll("url\\(",
                                ("url\\(" + name + "/"));

                    }
                    combinedCss.append(strLine);
                    combinedCss.append("\n");
                }
                // Close the input stream
                in.close();
            }
        }

        BufferedWriter out = new BufferedWriter(new FileWriter(SRCDIR
                + "/styles.css"));
        out.write(combinedCss.toString());
        out.close();

    }
}
