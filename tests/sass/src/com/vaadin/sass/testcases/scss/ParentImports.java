package com.vaadin.sass.testcases.scss;

import java.io.IOException;
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.junit.Test;
import org.w3c.css.sac.CSSException;

import com.vaadin.sass.ScssStylesheet;
import com.vaadin.sass.TestBase;
import com.vaadin.sass.handler.SCSSDocumentHandler;
import com.vaadin.sass.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.parser.Parser;
import com.vaadin.sass.tree.ImportNode;

public class ParentImports extends TestBase {

    String scss = "/scss/folder-test/parent-import.scss";
    String css = "/css/parent-import.css";

    @Test
    public void testParser() throws CSSException, IOException {
        Parser parser = new Parser();
        SCSSDocumentHandler handler = new SCSSDocumentHandlerImpl();
        parser.setDocumentHandler(handler);
        parser.parseStyleSheet(getClass().getResource(scss).getPath());
        ScssStylesheet root = handler.getStyleSheet();
        ImportNode importVariableNode = (ImportNode) root.getChildren().get(0);
        Assert.assertEquals("../folder-test2/variables.scss",
                importVariableNode.getUri());
        Assert.assertFalse(importVariableNode.isPureCssImport());

        ImportNode importURLNode = (ImportNode) root.getChildren().get(1);
        Assert.assertEquals("../folder-test2/url", importURLNode.getUri());
        Assert.assertFalse(importURLNode.isPureCssImport());

        ImportNode importImportNode = (ImportNode) root.getChildren().get(2);
        Assert.assertEquals("../folder-test2/base-imported.scss",
                importImportNode.getUri());
        Assert.assertFalse(importImportNode.isPureCssImport());
    }

    @Test
    public void testCompiler() throws CSSException, URISyntaxException,
            IOException {
        testCompiler(scss, css);
        Assert.assertEquals("Original CSS and parsed CSS doesn't match",
                comparisonCss, parsedScss);
    }
}
