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
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.runner.RunWith;

import com.vaadin.sass.testcases.scss.SassTestRunner.TestFactory;

@RunWith(SassTestRunner.class)
public class W3ConformanceTests extends AbstractDirectoryScanningSassTests {

    @Override
    protected URL getResourceURL(String path) {
        return getResourceURLInternal(path);
    }

    private static URL getResourceURLInternal(String path) {
        return AutomaticSassTests.class.getResource("/w3ctests" + path);
    }

    @TestFactory
    public static Collection<String> getScssResourceNames()
            throws URISyntaxException, IOException {
        return getScssResourceNames(getResourceURLInternal(""));
    }

    @Override
    protected File getCssFile(File scssFile) throws IOException {
        /*
         * We should really compare the result of unparse(parse(css)) to css,
         * but the comparator routine is currently too primitive.
         */
        // return scssFile;

        // no comparison step, just parse, in this test
        return null;
    }

    /*
     * Download W3C conformance tests for CSS 2.1 and CSS 3 (selectors),
     * extracts all CSS (style tags, inline styles, and linked stylesheets),
     * then tries to parse them. Since each CSS is valid SCSS, the parser should
     * accept them. As these are browser tests, some are intentionally
     * malformed, and must be excluded from the test suite.
     */

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Target directory not provided");
            return;
        }
        File targetDir = new File(args[0]);
        for (URI url : CSS21()) {
            extractCSS(url, targetDir);
        }
        for (URI url : CSS3Selectors()) {
            extractCSS(url, targetDir);
        }

    }

    public static Collection<URI> CSS21() throws Exception {
        /*
         * Tests explicitly excluded are listed below---case by case motivation
         * required!
         */
        final String[] excludelist = new String[] {
                // Unsupported character encoding UTF-16
                "http://test.csswg.org/suites/css2.1/20110323/html4/at-charset-utf16-be-002.htm",
                "http://test.csswg.org/suites/css2.1/20110323/html4/at-charset-utf16-be-003.htm",
                "http://test.csswg.org/suites/css2.1/20110323/html4/at-charset-utf16-le-002.htm",
                "http://test.csswg.org/suites/css2.1/20110323/html4/at-charset-utf16-le-003.htm",

                // Font family name contains (Asian?) cryptoglyphs
                "http://test.csswg.org/suites/css2.1/20110323/html4/font-family-name-010.htm",
                "http://test.csswg.org/suites/css2.1/20110323/html4/font-family-name-011.htm",
                "http://test.csswg.org/suites/css2.1/20110323/html4/font-family-name-015.htm",

                // Contains syntactically illegal CSS
                "http://test.csswg.org/suites/css2.1/20110323/html4/uri-013.htm",

                // Missing semicolon on line 29
                "http://test.csswg.org/suites/css2.1/20110323/html4/z-index-020.htm", };

        // Note: W3C test reference files also not included!
        return scrapeIndexForTests(
                "http://test.csswg.org/suites/css2.1/20110323/html4/reftest-toc.html",
                ".*[0-9][0-9][0-9][a-z]?\\.htm", Integer.MAX_VALUE,
                new LinkedHashSet<URI>() {
                    {
                        for (String s : excludelist) {
                            add(new URI(s));
                        }
                    }
                });
    }

    public static Collection<URI> CSS3Selectors() throws Exception {
        /*
         * Tests explicitly excluded are listed below---case by case motivation
         * required!
         */
        final String[] excludelist = new String[] {

                // Probable bug/limitation (filed as #12834)
                "http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-73.html",
                "http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-73b.html",
                "http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-74.html",
                "http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-74b.html",
                "http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-75.html",
                "http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-75b.html",
                "http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-76.html",
                "http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-76b.html",
                "http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-83.html",

                // Invalid CSS, although sass-lang compiler accepts (see #12835)
                "http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-154.html",

                // Invalid CSS? sass-lang compiler fails
                "http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-157.html",
                "http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-158.html",
                "http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/css3-modsel-183.html", };

        return scrapeIndexForTests(
                "http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/html/tests/",
                "css3-.*\\.html", Integer.MAX_VALUE, new LinkedHashSet<URI>() {
                    {
                        for (String s : excludelist) {
                            add(new URI(s));
                        }
                    }
                });
    }

    /*
     * Loads up to maxTest tests, excluding any URL in excludeUrls.
     */
    protected static Collection<URI> scrapeIndexForTests(String url,
            String regexp, int maxTests, Collection<URI> excludeUrls)
            throws Exception {

        URI baseUrl = new URI(url);
        Document doc = Jsoup.connect(url).timeout(10000).get();
        Elements elems = doc.select(String.format("a[href~=%s]", regexp));
        LinkedHashSet<URI> tests = new LinkedHashSet<URI>();
        for (Element e : elems) {
            URI testUrl = new URI(e.attr("href"));
            if (!testUrl.isAbsolute()) {
                testUrl = baseUrl.resolve(testUrl);
            }
            if (tests.size() < maxTests) {
                if (!excludeUrls.contains(testUrl)) {
                    tests.add(testUrl);
                }
            } else {
                break;
            }
        }

        return tests;
    }

    public static void extractCSS(final URI url, File targetdir)
            throws Exception {
        /*
         * For each test URL: 1) extract <style> tag contents 2) extract from
         * <link rel="stylesheet"> files 3) extract inline style attributes from
         * all elements and wrap the result in .style {}
         */

        Document doc = Jsoup.connect(url.toString()).timeout(20000).get();

        List<String> tests = new ArrayList<String>();

        for (Element e : doc.select("style[type=text/css]")) {
            tests.add(e.data());
        }

        for (Element e : doc
                .select("link[rel=stylesheet][href][type=text/css]")) {
            URI cssUri = new URI(e.attr("href"));
            if (!cssUri.isAbsolute()) {
                cssUri = url.resolve(cssUri);
            }
            String encoding = doc.outputSettings().charset().name();
            tests.add(IOUtils.toString(cssUri, encoding));
        }

        for (Element e : doc.select("*[style]")) {
            tests.add(String.format(".style { %s }", e.attr("style")));
        }

        for (final String test : tests) {
            targetdir.mkdirs();
            String logfile = String.format("%s.%d.scss",
                    FilenameUtils.getBaseName(url.toString()),
                    tests.indexOf(test));
            PrintStream dataLogger = new PrintStream(new File(targetdir,
                    logfile));

            dataLogger.println("/* Source: " + url + " */");
            dataLogger.println(test);

        }
    }
}
