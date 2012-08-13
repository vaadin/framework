/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.sass.testcases.scss;

import org.junit.Test;

import com.vaadin.sass.AbstractTestBase;

/**
 * Test for Microsoft specific CSS extensions.
 */
public class MicrosoftExtensions extends AbstractTestBase {
    String scss = "/scss/microsoft-extensions.scss";
    String css = "/css/microsoft-extensions.css";

    @Test
    public void testCompiler() throws Exception {
        testCompiler(scss, css);
    }
}
