package com.vaadin.sass.testcases.scss;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.w3c.css.sac.CSSException;

import com.vaadin.sass.AbstractTestBase;
import com.vaadin.sass.ScssStylesheet;
import com.vaadin.sass.handler.SCSSDocumentHandler;
import com.vaadin.sass.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.parser.Parser;
import com.vaadin.sass.selector.SelectorUtil;
import com.vaadin.sass.tree.BlockNode;

public class ParentSelector extends AbstractTestBase {
    String scss = "/scss/parent-selector.scss";
    String css = "/css/parent-selector.css";

    @Test
    public void testParser() throws CSSException, IOException {
        Parser parser = new Parser();
        SCSSDocumentHandler handler = new SCSSDocumentHandlerImpl();
        parser.setDocumentHandler(handler);
        parser.parseStyleSheet(getClass().getResource(scss).getPath());
        ScssStylesheet root = handler.getStyleSheet();
        BlockNode blockNode = (BlockNode) root.getChildren().get(0);
        Assert.assertEquals(4, blockNode.getChildren().size());
        BlockNode nestedBlock1 = (BlockNode) blockNode.getChildren().get(2);
        Assert.assertEquals("&:hover",
                SelectorUtil.toString(nestedBlock1.getSelectorList()));
        BlockNode nestedBlock2 = (BlockNode) blockNode.getChildren().get(3);
        Assert.assertEquals("body.firefox &",
                SelectorUtil.toString(nestedBlock2.getSelectorList()));
    }

    @Test
    public void testCompiler() throws Exception {
        testCompiler(scss, css);
    }
}
