/*
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.sass.testcases.scss;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.sass.internal.ScssStylesheet;

public abstract class AbstractDirectoryScanningSassTests extends TestCase {

    private String scssResourceName;

    protected AbstractDirectoryScanningSassTests(String scssResourceName) {
        this.scssResourceName = scssResourceName;
    }

    public static Collection<Object[]> getScssResourceNames(URL directoryUrl)
            throws URISyntaxException {
        List<Object[]> resources = new ArrayList<Object[]>();
        // temporary instance to enable subclasses to define where to scan for
        // files
        for (File scssFile : getScssFiles(directoryUrl)) {
            resources.add(new Object[] { scssFile.getName() });
        }
        return resources;
    }

    private static File[] getScssFiles(URL directoryUrl)
            throws URISyntaxException {
        URL sasslangUrl = directoryUrl;
        File sasslangDir = new File(sasslangUrl.toURI());
        File scssDir = new File(sasslangDir, "scss");
        assertTrue(scssDir.exists());

        return scssDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".scss");
            }
        });
    }

    protected abstract URL getResourceURL(String path);

    @Test
    public void compareScssWithCss() throws Exception {
        String referenceCss;
        File scssFile = getSassLangResourceFile(scssResourceName);
        File cssFile = getCssFile(scssFile);
        referenceCss = IOUtils.toString(new FileInputStream(cssFile));
        ScssStylesheet scssStylesheet = ScssStylesheet.get(scssFile
                .getAbsolutePath());
        scssStylesheet.compile();
        String parsedCss = scssStylesheet.toString();

        Assert.assertEquals("Original CSS and parsed CSS do not match for "
                + scssResourceName, normalize(referenceCss),
                normalize(parsedCss));
    }

    private String normalize(String css) {
        // Replace all whitespace characters with a single space
        css = css.replaceAll("[\n\r\t ]*", " ");
        // remove trailing whitespace
        css = css.replaceAll("[\n\r\t ]*$", "");
        return css;
    }

    private File getSassLangResourceFile(String resourceName)
            throws IOException, URISyntaxException {
        String base = "/scss/";
        String fullResourceName = base + resourceName;
        URL res = getResourceURL(fullResourceName);
        if (res == null) {
            throw new FileNotFoundException("Resource " + resourceName
                    + " not found (tried " + fullResourceName + ")");
        }
        return new File(res.toURI());
    }

    private File getCssFile(File scssFile) {
        return new File(scssFile.getAbsolutePath().replace("scss", "css"));
    }
}
