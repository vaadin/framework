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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.w3c.css.sac.CSSException;

import com.vaadin.sass.testcases.scss.SassTestRunner.TestFactory;

@RunWith(SassTestRunner.class)
public class SassLangTestsBroken extends AbstractDirectoryScanningSassTests {

    @Override
    protected URL getResourceURL(String path) {
        return getResourceURLInternal(path);
    }

    private static URL getResourceURLInternal(String path) {
        URL url = SassLangTestsBroken.class.getResource("/sasslangbroken"
                + path);
        if (url == null) {
            throw new RuntimeException(
                    "Could not locate /sasslangbroken using classloader");
        }
        return url;
    }

    @TestFactory
    public static Collection<String> getScssResourceNames()
            throws URISyntaxException, IOException {
        return getScssResourceNames(getResourceURLInternal(""));
    }

    @Override
    public void compareScssWithCss(String scssResourceName) throws Exception {
        boolean success = false;
        try {
            super.compareScssWithCss(scssResourceName);
            success = true;
        } catch (CSSException e) {
            // this is an expected outcome
        } catch (AssertionError e) {
            // this is an expected outcome
        }
        if (success) {
            Assert.fail("Test "
                    + scssResourceName
                    + " from sasslangbroken that was expected to fail has been fixed. Please move the corresponding CSS and SCSS files to sasslang.");
        }
    }

}
