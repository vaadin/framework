package com.vaadin.sass.testcases.scss;

import java.io.IOException;

import org.junit.Test;
import org.w3c.css.sac.CSSException;

import com.vaadin.sass.AbstractTestBase;

public class RemoveDirective extends AbstractTestBase {

    String scss = "/scss/remove-directive.scss";
    String css = "/css/remove-directive.css";

    @Test
    public void testParser() throws CSSException, IOException {

    }

    @Test
    public void testCompiler() throws Exception {
        testCompiler(scss, css);
    }

}
