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

import org.junit.Assert;
import org.junit.Test;
import org.w3c.css.sac.CSSException;

import com.vaadin.sass.AbstractTestBase;
import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.handler.SCSSDocumentHandler;
import com.vaadin.sass.internal.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.internal.parser.Parser;
import com.vaadin.sass.internal.tree.BlockNode;

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
        Assert.assertEquals(5, blockNode.getChildren().size());
        BlockNode nestedBlock1 = (BlockNode) blockNode.getChildren().get(3);
        Assert.assertEquals("&:hover", nestedBlock1.getSelectorList().get(0));
        BlockNode nestedBlock2 = (BlockNode) blockNode.getChildren().get(4);
        Assert.assertEquals("body.firefox &", nestedBlock2.getSelectorList()
                .get(0));
    }

    @Test
    public void testCompiler() throws Exception {
        testCompiler(scss, css);
    }
}
