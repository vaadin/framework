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

package com.vaadin.sass;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;

import com.vaadin.sass.handler.SCSSDocumentHandler;
import com.vaadin.sass.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.handler.SCSSErrorHandler;
import com.vaadin.sass.parser.Parser;
import com.vaadin.sass.resolver.ScssStylesheetResolver;
import com.vaadin.sass.resolver.VaadinResolver;
import com.vaadin.sass.tree.Node;
import com.vaadin.sass.tree.VariableNode;
import com.vaadin.sass.visitor.BlockVisitor;
import com.vaadin.sass.visitor.EachVisitor;
import com.vaadin.sass.visitor.ExtendVisitor;
import com.vaadin.sass.visitor.IfElseVisitor;
import com.vaadin.sass.visitor.ImportVisitor;
import com.vaadin.sass.visitor.MixinVisitor;
import com.vaadin.sass.visitor.NestPropertiesVisitor;
import com.vaadin.sass.visitor.ParentSelectorVisitor;
import com.vaadin.sass.visitor.VariableVisitor;
import com.vaadin.sass.visitor.Visitor;

public class ScssStylesheet extends Node {

    private static final long serialVersionUID = 3849790204404961608L;

    /**
     * Read in a file SCSS and parse it into a ScssStylesheet
     * 
     * @param file
     * @throws IOException
     */
    public ScssStylesheet() {
        super();
    }

    /**
     * Main entry point for the SASS compiler. Takes in a file and builds upp a
     * ScssStylesheet tree out of it. Calling compile() on it will transform
     * SASS into CSS. Calling toString() will print out the SCSS/CSS.
     * 
     * @param file
     * @return
     * @throws CSSException
     * @throws IOException
     */
    public static ScssStylesheet get(String identifier) throws CSSException,
            IOException {
        File file = new File(identifier);
        file = file.getCanonicalFile();

        SCSSDocumentHandler handler = new SCSSDocumentHandlerImpl();
        ScssStylesheet stylesheet = handler.getStyleSheet();

        InputSource source = stylesheet.resolveStylesheet(identifier);
        if (source == null) {
            return null;
        }

        Parser parser = new Parser();
        parser.setErrorHandler(new SCSSErrorHandler());
        parser.setDocumentHandler(handler);
        parser.parseStyleSheet(source);

        return stylesheet;
    }

    private static ScssStylesheetResolver[] resolvers = null;

    public static void setStylesheetResolvers(
            ScssStylesheetResolver... styleSheetResolvers) {
        resolvers = Arrays.copyOf(styleSheetResolvers,
                styleSheetResolvers.length);
    }

    public InputSource resolveStylesheet(String identifier) {
        if (resolvers == null) {
            setStylesheetResolvers(new VaadinResolver());
        }

        for (ScssStylesheetResolver resolver : resolvers) {
            InputSource source = resolver.resolve(identifier);
            if (source != null) {
                File f = new File(source.getURI());
                setFileName(f.getParent());
                return source;
            }
        }

        return null;
    }

    /**
     * Applies all the visitors and compiles SCSS into Css.
     * 
     * @throws Exception
     */
    public void compile() throws Exception {
        ScssStylesheet scssStylesheet = this;
        List<Visitor> visitors = new ArrayList<Visitor>();
        visitors.add(new ImportVisitor());
        visitors.add(new VariableVisitor());
        visitors.add(new MixinVisitor());
        visitors.add(new IfElseVisitor());
        visitors.add(new ParentSelectorVisitor());
        visitors.add(new BlockVisitor());
        visitors.add(new NestPropertiesVisitor());
        visitors.add(new ExtendVisitor());
        visitors.add(new EachVisitor());
        for (Visitor visitor : visitors) {
            visitor.traverse(this);
        }
    }

    /**
     * Prints out the current state of the node tree. Will return SCSS before
     * compile and CSS after.
     * 
     * For now this is an own method with it's own implementation that most node
     * types will implement themselves.
     */
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("");
        if (children.size() > 0) {
            string.append(children.get(0).toString());
        }
        String delimeter = "\n\n";
        if (children.size() > 1) {
            for (int i = 1; i < children.size(); i++) {
                String childString = children.get(i).toString();
                if (childString != null) {
                    string.append(delimeter).append(childString);
                }
            }
        }
        String output = string.toString();
        return output;
    }

    public void addChild(int index, VariableNode node) {
        if (node != null) {
            children.add(index, node);
        }
    }

}
