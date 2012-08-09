/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.sass.testcases.scss;

import java.io.IOException;
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.junit.Test;
import org.w3c.css.sac.CSSException;

import com.vaadin.sass.AbstractTestBase;

/**
 * Test for missing and extraneous semicolon handling.
 */
public class Semicolons extends AbstractTestBase {
    String scss = "/scss/semicolons.scss";
    String css = "/scss/semicolons.css";

    @Test
    public void testCompiler() throws CSSException, URISyntaxException,
            IOException {
        testCompiler(scss, css);
        Assert.assertEquals("Original CSS and parsed CSS doesn't match",
                comparisonCss, parsedScss);
    }
}
