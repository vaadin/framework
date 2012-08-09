package com.vaadin.sass.testcases.scss;

import java.io.IOException;
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.junit.Test;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.LexicalUnit;

import com.vaadin.sass.ScssStylesheet;
import com.vaadin.sass.AbstractTestBase;
import com.vaadin.sass.handler.SCSSDocumentHandler;
import com.vaadin.sass.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.parser.Parser;
import com.vaadin.sass.tree.BlockNode;
import com.vaadin.sass.tree.MediaNode;
import com.vaadin.sass.tree.MixinDefNode;
import com.vaadin.sass.tree.MixinNode;

public class Mixins extends AbstractTestBase {

    String scss = "/scss/mixins.scss";
    String css = "/css/mixins.css";

    @Test
    public void testParser() throws CSSException, URISyntaxException,
            IOException {
        Parser parser = new Parser();
        SCSSDocumentHandler handler = new SCSSDocumentHandlerImpl();
        parser.setDocumentHandler(handler);
        parser.parseStyleSheet(getClass().getResource(scss).getPath());
        ScssStylesheet root = handler.getStyleSheet();

        MixinDefNode mixinDefNode0 = (MixinDefNode) root.getChildren().get(0);
        Assert.assertEquals("font-settings", mixinDefNode0.getName());
        Assert.assertTrue(mixinDefNode0.getArglist().isEmpty());
        Assert.assertEquals(3, mixinDefNode0.getChildren().size());

        MixinDefNode mixinDefNode1 = (MixinDefNode) root.getChildren().get(1);
        Assert.assertEquals("rounded-borders", mixinDefNode1.getName());
        Assert.assertEquals(2, mixinDefNode1.getArglist().size());
        Assert.assertEquals("thickness", mixinDefNode1.getArglist().get(0)
                .getName());
        Assert.assertEquals("radius", mixinDefNode1.getArglist().get(1)
                .getName());
        Assert.assertEquals(LexicalUnit.SAC_PIXEL, mixinDefNode1.getArglist()
                .get(1).getExpr().getLexicalUnitType());
        Assert.assertEquals(3f, mixinDefNode1.getArglist().get(1).getExpr()
                .getFloatValue());

        Assert.assertEquals(4, mixinDefNode1.getChildren().size());

        BlockNode mainBlockNode = (BlockNode) root.getChildren().get(2);
        Assert.assertEquals(2, mainBlockNode.getChildren().size());
        MixinNode mixinNode0MainBlock = (MixinNode) mainBlockNode.getChildren()
                .get(0);
        Assert.assertEquals("rounded-borders", mixinNode0MainBlock.getName());
        Assert.assertEquals(1f, mixinNode0MainBlock.getArglist().get(0)
                .getFloatValue());
        Assert.assertEquals(LexicalUnit.SAC_PIXEL, mixinNode0MainBlock
                .getArglist().get(0).getLexicalUnitType());
        MixinNode mixinNOde1MainBlock = (MixinNode) mainBlockNode.getChildren()
                .get(1);
        Assert.assertEquals("font-settings", mixinNOde1MainBlock.getName());
        Assert.assertTrue(mixinNOde1MainBlock.getArglist().isEmpty());

        MixinNode mixinNode1MainBlock = (MixinNode) mainBlockNode.getChildren()
                .get(1);
        Assert.assertTrue(mixinNode1MainBlock.getArglist().isEmpty());

        BlockNode footerBlockNode = (BlockNode) root.getChildren().get(3);
        MixinNode mixinNodeFooterBlock = (MixinNode) footerBlockNode
                .getChildren().get(0);
        Assert.assertEquals(2f, mixinNodeFooterBlock.getArglist().get(0)
                .getFloatValue());
        Assert.assertEquals("px", mixinNodeFooterBlock.getArglist().get(0)
                .getDimensionUnitText());

        Assert.assertEquals(10f, mixinNodeFooterBlock.getArglist().get(1)
                .getFloatValue());
        Assert.assertEquals("px", mixinNodeFooterBlock.getArglist().get(1)
                .getDimensionUnitText());

        Assert.assertTrue(root.getChildren().get(4) instanceof MixinDefNode);
        Assert.assertTrue(root.getChildren().get(4).getChildren().get(3) instanceof MediaNode);
        Assert.assertTrue(root.getChildren().get(4).getChildren().get(4) instanceof MixinNode);

        MixinNode topLevelMixin = (MixinNode) root.getChildren().get(5);
        Assert.assertEquals("layout", topLevelMixin.getName());

    }

    @Test
    public void testCompiler() throws CSSException, URISyntaxException,
            IOException {
        testCompiler(scss, css);
        Assert.assertEquals("Original CSS and parsed CSS doesn't match",
                comparisonCss, parsedScss);
    }

}
