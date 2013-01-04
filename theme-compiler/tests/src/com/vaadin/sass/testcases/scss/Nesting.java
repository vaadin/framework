/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.sass.testcases.scss;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.w3c.css.sac.CSSException;

import com.vaadin.sass.AbstractTestBase;
import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.handler.SCSSDocumentHandler;
import com.vaadin.sass.internal.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.internal.parser.Parser;
import com.vaadin.sass.internal.tree.BlockNode;

public class Nesting extends AbstractTestBase {

    String scss = "/scss/nesting.scss";
    String css = "/css/nesting.css";

    @Test
    public void testParser() throws CSSException, IOException {
        Parser parser = new Parser();
        SCSSDocumentHandler handler = new SCSSDocumentHandlerImpl();
        parser.setDocumentHandler(handler);
        parser.parseStyleSheet(getClass().getResource(scss).getPath());
        ScssStylesheet root = handler.getStyleSheet();
        Assert.assertEquals(6, root.getChildren().size());

        BlockNode blockNode0 = (BlockNode) root.getChildren().get(0);
        Assert.assertEquals(2, blockNode0.getChildren().size());
        BlockNode nestedBlock0 = (BlockNode) blockNode0.getChildren().get(1);
        org.junit.Assert.assertEquals(1, nestedBlock0.getChildren().size());

        BlockNode blockNode1 = (BlockNode) root.getChildren().get(1);
        Assert.assertEquals(2, blockNode1.getChildren().size());
        BlockNode nestedBlockInBlock1 = (BlockNode) blockNode1.getChildren()
                .get(1);
        Assert.assertEquals(1, nestedBlockInBlock1.getChildren().size());

        BlockNode blockNode2 = (BlockNode) root.getChildren().get(2);
        Assert.assertEquals(2, blockNode2.getChildren().size());
        BlockNode nestedBlockInBlock2 = (BlockNode) blockNode2.getChildren()
                .get(1);
        Assert.assertEquals(1, nestedBlockInBlock2.getChildren().size());

        BlockNode blockNode3 = (BlockNode) root.getChildren().get(3);
        Assert.assertEquals(2, blockNode3.getChildren().size());
        BlockNode nestedBlockInBlock3 = (BlockNode) blockNode3.getChildren()
                .get(1);
        Assert.assertEquals(2, nestedBlockInBlock3.getChildren().size());
        BlockNode nestednestedBlockInBlock3 = (BlockNode) nestedBlockInBlock3
                .getChildren().get(1);
        Assert.assertEquals(1, nestednestedBlockInBlock3.getChildren().size());

        BlockNode blockNode4 = (BlockNode) root.getChildren().get(4);
        Assert.assertEquals(2, blockNode4.getChildren().size());
        BlockNode nestedBlockInBlock4 = (BlockNode) blockNode3.getChildren()
                .get(1);
        Assert.assertEquals(2, nestedBlockInBlock4.getChildren().size());
        BlockNode nestednestedBlockInBlock4 = (BlockNode) nestedBlockInBlock3
                .getChildren().get(1);
        Assert.assertEquals(1, nestednestedBlockInBlock4.getChildren().size());

        // the parsing of the last block is not checked in detail
    }

    @Test
    public void testCompiler() throws Exception {
        testCompiler(scss, css);
    }
}
