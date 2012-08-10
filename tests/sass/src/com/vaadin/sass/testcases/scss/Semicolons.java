/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.sass.testcases.scss;

import org.junit.Test;

import com.vaadin.sass.AbstractTestBase;

/**
 * Test for missing and extraneous semicolon handling.
 */
public class Semicolons extends AbstractTestBase {
    String scss = "/scss/semicolons.scss";
    String css = "/css/semicolons.css";

    @Test
    public void testCompiler() throws Exception {
        testCompiler(scss, css);
    }
}
