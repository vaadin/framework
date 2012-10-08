package com.vaadin.sass.testcases.scss;

import org.junit.Test;

import com.vaadin.sass.AbstractTestBase;

public class ListModify extends AbstractTestBase {

    String scss = "/scss/listmodify.scss";
    String css = "/css/listmodify.css";

    @Test
    public void testCompiler() throws Exception {
        testCompiler(scss, css);
    }

}
