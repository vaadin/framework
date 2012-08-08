package com.vaadin.sass;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.css.sac.CSSException;

import com.vaadin.sass.handler.SCSSDocumentHandler;
import com.vaadin.sass.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.parser.Parser;
import com.vaadin.sass.tree.Node;
import com.vaadin.sass.visitor.BlockVisitor;
import com.vaadin.sass.visitor.ExtendVisitor;
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
    public static ScssStylesheet get(File file) throws CSSException,
            IOException {
        Parser parser = new Parser();
        SCSSDocumentHandler handler = new SCSSDocumentHandlerImpl();
        file = file.getCanonicalFile();
        handler.getStyleSheet().setFileName(file.getAbsoluteFile().getParent());
        parser.setDocumentHandler(handler);
        parser.parseStyleSheet(file.getAbsolutePath());
        return handler.getStyleSheet();
    }

    /**
     * Applies all the visitors and compiles SCSS into Css.
     * 
     * @throws Exception
     */
    public void compile() throws Exception {
        List<Visitor> visitors = new ArrayList<Visitor>();
        visitors.add(new ImportVisitor());
        visitors.add(new MixinVisitor());
        visitors.add(new VariableVisitor());
        visitors.add(new ParentSelectorVisitor());
        visitors.add(new BlockVisitor());
        visitors.add(new NestPropertiesVisitor());
        visitors.add(new ExtendVisitor());
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
}
