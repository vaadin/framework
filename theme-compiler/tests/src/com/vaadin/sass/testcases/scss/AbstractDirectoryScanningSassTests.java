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

package com.vaadin.sass.testcases.scss;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.handler.SCSSDocumentHandler;
import com.vaadin.sass.internal.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.internal.handler.SCSSErrorHandler;
import com.vaadin.sass.testcases.scss.SassTestRunner.FactoryTest;

public abstract class AbstractDirectoryScanningSassTests {

    public static Collection<String> getScssResourceNames(URL directoryUrl)
            throws URISyntaxException, IOException {
        List<String> resources = new ArrayList<String>();
        for (String scssFile : getScssFiles(directoryUrl)) {
            resources.add(scssFile);
        }
        return resources;
    }

    private static List<String> getScssFiles(URL directoryUrl)
            throws URISyntaxException, IOException {
        URL sasslangUrl = directoryUrl;
        File sasslangDir = new File(sasslangUrl.toURI());
        File scssDir = new File(sasslangDir, "scss");
        Assert.assertTrue(scssDir.exists());

        List<File> scssFiles = new ArrayList<File>();
        addScssFilesRecursively(scssDir, scssFiles);

        List<String> scssRelativeNames = new ArrayList<String>();
        for (File f : scssFiles) {
            String relativeName = f.getCanonicalPath().substring(
                    scssDir.getCanonicalPath().length() + 1);
            scssRelativeNames.add(relativeName);
        }
        return scssRelativeNames;
    }

    private static void addScssFilesRecursively(File scssDir,
            List<File> scssFiles) {
        for (File f : scssDir.listFiles()) {
            if (f.isDirectory()) {
                addScssFilesRecursively(f, scssFiles);
            } else if (f.getName().endsWith(".scss")
                    && !f.getName().startsWith("_")) {
                scssFiles.add(f);
            }
        }
    }

    protected abstract URL getResourceURL(String path);

    @FactoryTest
    public void compareScssWithCss(String scssResourceName) throws Exception {
        File scssFile = getSassLangResourceFile(scssResourceName);

        SCSSDocumentHandler documentHandler = new SCSSDocumentHandlerImpl();
        SCSSErrorHandler errorHandler = new SCSSErrorHandler() {
            @Override
            public void error(CSSParseException arg0) throws CSSException {
                super.error(arg0);
                Assert.fail(arg0.getMessage());
            }

            @Override
            public void fatalError(CSSParseException arg0) throws CSSException {
                super.error(arg0);
                Assert.fail(arg0.getMessage());
            }
        };

        ScssStylesheet scssStylesheet = ScssStylesheet.get(
                scssFile.getCanonicalPath(), null, documentHandler,
                errorHandler);
        scssStylesheet.compile();
        String parsedCss = scssStylesheet.toString();

        if (getCssFile(scssFile) != null) {
            String referenceCss = IOUtils.toString(new FileInputStream(
                    getCssFile(scssFile)));
            String normalizedReference = normalize(referenceCss);
            String normalizedParsed = normalize(parsedCss);

            Assert.assertEquals("Original CSS and parsed CSS do not match for "
                    + scssResourceName, normalizedReference, normalizedParsed);
        }
    }

    private String normalize(String css) {
        // Insert whitespace at each point
        css = css.replaceAll("[\n\r\t ]*", " ");
        // Replace multiple whitespace characters with a single space to compact
        css = css.replaceAll("[\n\r\t ]+", " ");
        // remove initial whitespace
        css = css.replaceAll("^[\n\r\t ]*", "");
        // remove trailing whitespace
        css = css.replaceAll("[\n\r\t ]*$", "");
        css = css.replaceAll(";", ";\n");
        css = css.replaceAll("\\{", "\\{\n");
        css = css.replaceAll("}", "}\n");
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

    protected File getCssFile(File scssFile) throws IOException {
        return new File(scssFile.getCanonicalPath().replace("scss", "css"));
    }
}
