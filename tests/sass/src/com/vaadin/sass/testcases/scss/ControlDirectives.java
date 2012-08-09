package com.vaadin.sass.testcases.scss;

import java.io.IOException;
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.junit.Test;
import org.w3c.css.sac.CSSException;

import com.vaadin.sass.ScssStylesheet;
import com.vaadin.sass.AbstractTestBase;
import com.vaadin.sass.handler.SCSSDocumentHandler;
import com.vaadin.sass.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.parser.Parser;

public class ControlDirectives extends AbstractTestBase {

    String scss = "/scss/control-directives.scss";
    String css = "/css/control-directives.css";

    @Test
    public void testParser() throws CSSException, IOException {
        Parser parser = new Parser();
        SCSSDocumentHandler handler = new SCSSDocumentHandlerImpl();
        parser.setDocumentHandler(handler);
        parser.parseStyleSheet(getClass().getResource(scss).getPath());
        ScssStylesheet root = handler.getStyleSheet();
        Assert.assertNotNull(root);
        Assert.fail("Implement assert nodes");
    }

    @Test
    public void testCompiler() throws CSSException, URISyntaxException,
            IOException {
        testCompiler(scss, css);
        Assert.assertEquals("Original CSS and parsed CSS doesn't match",
                comparisonCss, parsedScss);
    }
}
