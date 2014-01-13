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

package com.vaadin.sass;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.w3c.css.sac.CSSException;

import com.vaadin.sass.internal.ScssStylesheet;

public abstract class AbstractTestBase {

    public static final String CR = "\r";

    protected ScssStylesheet stylesheet;
    protected String originalScss;
    protected String parsedScss;
    protected String comparisonCss;

    public ScssStylesheet getStyleSheet(String filename)
            throws URISyntaxException, CSSException, IOException {
        File file = getFile(filename);
        stylesheet = ScssStylesheet.get(file.getAbsolutePath());
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
        return IOUtils.toString(new FileReader(file));
    }

    public void testParser(String file) throws CSSException, IOException,
            URISyntaxException {
        originalScss = getFileContent(file);
        originalScss = originalScss.replaceAll(CR, "");
        ScssStylesheet sheet = getStyleSheet(file);
        parsedScss = sheet.toString();
        parsedScss = parsedScss.replace(CR, "");
        Assert.assertEquals("Original CSS and parsed CSS do not match",
                originalScss, parsedScss);
    }

    public void testCompiler(String scss, String css) throws Exception {
        comparisonCss = getFileContent(css);
        comparisonCss = comparisonCss.replaceAll(CR, "");
        ScssStylesheet sheet = getStyleSheet(scss);
        sheet.compile();
        parsedScss = sheet.toString();
        parsedScss = parsedScss.replaceAll(CR, "");
        Assert.assertEquals("Original CSS and parsed CSS do not match",
                comparisonCss, parsedScss);
    }
}
