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
import com.vaadin.sass.tree.BlockNode;
import com.vaadin.sass.tree.NestPropertiesNode;
import com.vaadin.sass.tree.RuleNode;

public class NestedProperties extends TestBase {
    String scss = "/scss/nested-properties.scss";
    String css = "/css/nested-properties.css";

    @Test
    public void testParser() throws CSSException, IOException {
        Parser parser = new Parser();
        SCSSDocumentHandler handler = new SCSSDocumentHandlerImpl();
        parser.setDocumentHandler(handler);
        parser.parseStyleSheet(getClass().getResource(scss).getPath());
        ScssStylesheet root = handler.getStyleSheet();
        Assert.assertEquals(1, root.getChildren().size());

        BlockNode blockNode = (BlockNode) root.getChildren().get(0);
        Assert.assertEquals(1, blockNode.getChildren().size());

        NestPropertiesNode nestPropertiesNode = (NestPropertiesNode) blockNode
                .getChildren().get(0);
        Assert.assertEquals("font", nestPropertiesNode.getName());
        RuleNode nestedProperty0 = (RuleNode) nestPropertiesNode.getChildren()
                .get(0);
        RuleNode nestedProperty1 = (RuleNode) nestPropertiesNode.getChildren()
                .get(1);
        RuleNode nestedProperty2 = (RuleNode) nestPropertiesNode.getChildren()
                .get(2);
        Assert.assertEquals("family", nestedProperty0.getVariable());
        Assert.assertEquals("weight", nestedProperty1.getVariable());
        Assert.assertEquals("size", nestedProperty2.getVariable());
    }

    @Test
    public void testCompiler() throws CSSException, URISyntaxException,
            IOException {
        testCompiler(scss, css);
        Assert.assertEquals("Original CSS and parsed CSS doesn't match",
                comparisonCss, parsedScss);
    }
}
