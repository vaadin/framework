/*
 * Copyright 2011 Vaadin Ltd.
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
import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;
import org.w3c.css.sac.CSSException;

import com.vaadin.sass.AbstractTestBase;
import com.vaadin.sass.ScssStylesheet;
import com.vaadin.sass.handler.SCSSDocumentHandler;
import com.vaadin.sass.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.parser.Parser;
import com.vaadin.sass.tree.Node;

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

        ArrayList<Node> children = root.getChildren();
        Assert.assertEquals(6, root.getChildren().size());
        //
        // Assert.assertTrue(children.get(1) instanceof EachDefNode);
        // Assert.assertTrue(children.get(2) instanceof BlockNode);
        // Assert.assertTrue(children.get(3) instanceof BlockNode);
        // Assert.assertTrue(children.get(4) instanceof BlockNode);
        // Assert.assertTrue(children.get(5) instanceof MixinDefNode);
        //
        // Assert.assertTrue(children.get(2).getChildren().get(0) instanceof
        // IfElseDefNode);
        // Assert.assertTrue(children.get(3).getChildren().get(0) instanceof
        // IfElseDefNode);
        // Assert.assertTrue(children.get(4).getChildren().get(0) instanceof
        // IfElseDefNode);
        // Assert.assertTrue(!(children.get(5).getChildren().get(0) instanceof
        // IfElseDefNode));
        //
        // Assert.assertEquals(1, children.get(1).getChildren().size());

    }

    @Test
    public void testCompiler() throws Exception {
        testCompiler(scss, css);
    }
}
