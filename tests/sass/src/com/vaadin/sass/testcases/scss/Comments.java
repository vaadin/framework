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
import com.vaadin.sass.tree.CommentNode;

public class Comments extends AbstractTestBase {
    String scss = "/scss/comments.scss";
    String css = "/scss/comments.css";

    @Test
    public void testParser() throws CSSException, URISyntaxException,
            IOException {
        Parser parser = new Parser();
        SCSSDocumentHandler handler = new SCSSDocumentHandlerImpl();
        parser.setDocumentHandler(handler);
        parser.parseStyleSheet(getClass().getResource(scss).getPath());
        ScssStylesheet root = handler.getStyleSheet();
        Assert.assertNotNull(root);
        Assert.assertEquals(5, root.getChildren().size());
        Assert.assertTrue(root.getChildren().get(0) instanceof CommentNode);
        Assert.assertTrue(root.getChildren().get(2) instanceof CommentNode);
    }

    @Test
    public void testCompiler() throws CSSException, URISyntaxException,
            IOException {
        testCompiler(scss, css);
        Assert.assertEquals("Original CSS and parsed CSS doesn't match",
                comparisonCss, parsedScss);
    }
}
