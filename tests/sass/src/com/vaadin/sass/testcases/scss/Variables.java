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
import com.vaadin.sass.parser.SCSSLexicalUnit;
import com.vaadin.sass.tree.BlockNode;
import com.vaadin.sass.tree.RuleNode;
import com.vaadin.sass.tree.VariableNode;

public class Variables extends AbstractTestBase {

    String scss = "/scss/variables.scss";
    String css = "/css/variables.css";

    @Test
    public void testParser() throws CSSException, IOException {
        Parser parser = new Parser();
        SCSSDocumentHandler handler = new SCSSDocumentHandlerImpl();
        parser.setDocumentHandler(handler);
        parser.parseStyleSheet(getClass().getResource(scss).getPath());
        ScssStylesheet root = handler.getStyleSheet();
        Assert.assertEquals(5, root.getChildren().size());

        VariableNode varNode1 = (VariableNode) root.getChildren().get(0);
        Assert.assertEquals("blue", varNode1.getName());
        // Assert.assertEquals("blue", varNode1.getExpr().);

        VariableNode varNode2 = (VariableNode) root.getChildren().get(1);
        Assert.assertEquals("margin", varNode2.getName());
        Assert.assertEquals(8f, varNode2.getExpr().getFloatValue());
        Assert.assertEquals("px", varNode2.getExpr().getDimensionUnitText());

        VariableNode varNode3 = (VariableNode) root.getChildren().get(2);
        Assert.assertEquals("chameleon-font-family", varNode3.getName());

        BlockNode blockNode1 = (BlockNode) root.getChildren().get(3);
        Assert.assertEquals(4, blockNode1.getChildren().size());
        RuleNode ruleNode1Block1 = (RuleNode) blockNode1.getChildren().get(0);
        Assert.assertEquals("border-color", ruleNode1Block1.getVariable());
        Assert.assertEquals(SCSSLexicalUnit.SCSS_VARIABLE, ruleNode1Block1
                .getValue().getLexicalUnitType());
        Assert.assertEquals("blue", ruleNode1Block1.getValue().getStringValue());

        VariableNode varNode1Block1 = (VariableNode) blockNode1.getChildren()
                .get(1);
        Assert.assertEquals("blue", varNode1Block1.getName());

        RuleNode ruleNode2Block1 = (RuleNode) blockNode1.getChildren().get(2);
        Assert.assertEquals("color", ruleNode2Block1.getVariable());
        Assert.assertEquals(SCSSLexicalUnit.SCSS_VARIABLE, ruleNode2Block1
                .getValue().getLexicalUnitType());
        Assert.assertEquals("blue", ruleNode2Block1.getValue().getStringValue());

        BlockNode blockNode2 = (BlockNode) root.getChildren().get(4);
        RuleNode ruleNode1Block2 = (RuleNode) blockNode2.getChildren().get(0);
        Assert.assertEquals("padding", ruleNode1Block2.getVariable());
        Assert.assertEquals(SCSSLexicalUnit.SCSS_VARIABLE, ruleNode1Block2
                .getValue().getLexicalUnitType());
        Assert.assertEquals("margin", ruleNode1Block2.getValue()
                .getStringValue());

        RuleNode ruleNode2Block2 = (RuleNode) blockNode2.getChildren().get(1);
        Assert.assertEquals("margin", ruleNode2Block2.getVariable());
        Assert.assertEquals(SCSSLexicalUnit.SCSS_VARIABLE, ruleNode2Block2
                .getValue().getLexicalUnitType());
        Assert.assertEquals("margin", ruleNode2Block2.getValue()
                .getStringValue());

        RuleNode ruleNode3Block2 = (RuleNode) blockNode2.getChildren().get(2);
        Assert.assertEquals("border-color", ruleNode3Block2.getVariable());
        Assert.assertEquals(SCSSLexicalUnit.SCSS_VARIABLE, ruleNode1Block2
                .getValue().getLexicalUnitType());
        Assert.assertEquals("blue", ruleNode3Block2.getValue().getStringValue());
    }

    @Test
    public void testCompiler() throws CSSException, URISyntaxException,
            IOException {
        testCompiler(scss, css);
        Assert.assertEquals("Original CSS and parsed CSS doesn't match",
                comparisonCss, parsedScss);
    }
}
