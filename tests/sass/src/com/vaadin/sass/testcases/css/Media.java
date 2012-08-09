/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.sass.testcases.css;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;
import org.w3c.css.sac.CSSException;

import com.vaadin.sass.AbstractTestBase;

public class Media extends AbstractTestBase {

    String css = "/basic/media.css";

    @Test
    public void testParser() throws CSSException, URISyntaxException,
            IOException {
        testParser(css);
    }
}
