package com.vaadin.sass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

import org.w3c.css.sac.CSSException;

public abstract class AbstractTestBase {

    protected ScssStylesheet stylesheet;
    protected String originalScss;
    protected String parsedScss;
    protected String comparisonCss;

    public ScssStylesheet getStyleSheet(String filename)
            throws URISyntaxException, CSSException, IOException {
        File file = getFile(filename);
        stylesheet = ScssStylesheet.get(file);
        return stylesheet;
    }

    public File getFile(String filename) throws URISyntaxException,
            CSSException, IOException {
        return new File(getClass().getResource(filename).toURI());
    }

    public String getFileContent(String filename) throws IOException,
            CSSException, URISyntaxException {
        File file = getFile(filename);
        return getFileContent(file);
    }

    /**
     * Read in the full content of a file into a string.
     * 
     * @param file
     *            the file to be read
     * @return a String with the content of the
     * @throws IOException
     *             when file reading fails
     */
    public String getFileContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();

        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = null;
        // Handle the first line separately to get the right amount of line
        // separators in the loop
        if ((line = bufferedReader.readLine()) != null) {
            content.append(line);
        }
        // Handle the rest of the lines
        while ((line = bufferedReader.readLine()) != null) {
            content.append(System.getProperty("line.separator"));
            content.append(line);
        }
        bufferedReader.close();
        return content.toString();
    }

    public boolean testParser(String file) throws CSSException, IOException,
            URISyntaxException {
        originalScss = getFileContent(file);
        ScssStylesheet sheet = getStyleSheet(file);
        parsedScss = sheet.toString();
        return parsedScss.equals(originalScss);
    }

    public boolean testCompiler(String scss, String css) {
        try {
            comparisonCss = getFileContent(css);
            ScssStylesheet sheet = getStyleSheet(scss);
            sheet.compile();
            parsedScss = sheet.toString();
        } catch (Exception e) {
            return false;
        }
        return parsedScss.equals(comparisonCss);
    }
}
