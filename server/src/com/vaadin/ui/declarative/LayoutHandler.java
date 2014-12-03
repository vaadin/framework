/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.ui.declarative;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;

import com.vaadin.ui.DesignSynchronizable;

/**
 * LayoutHandler is used for parsing a component hierarchy from an html file
 * and, conversely, for generating an html tree representation corresponding to
 * a given component hierarchy. For both parsing and tree generation the
 * component hierarchy must contain a single root.
 * 
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class LayoutHandler {
    /**
     * Constructs a component hierarchy from the design specified as an html
     * document. The component hierarchy must contain exactly one top-level
     * Component. The component should be located under <body>, but also invalid
     * html containing the hierarchy without <html>, <head> and <body> tags is
     * accepted.
     * 
     * @param html
     *            the html document describing the component design
     * @return the DesignContext created while traversing the tree. The
     *         top-level component of the created component hierarchy can be
     *         accessed using result.getRootComponent(), where result is the
     *         object returned by this method.
     * @throws IOException
     */
    public static DesignContext parse(InputStream html) {
        Document doc;
        try {
            doc = Jsoup.parse(html, "UTF-8", "", Parser.htmlParser());
        } catch (IOException e) {
            throw new DesignException("The html document cannot be parsed.");
        }
        return parse(doc);
    }

    /**
     * Constructs a component hierarchy from the design specified as an html
     * document given as a string. The component hierarchy must contain exactly
     * one top-level Component. The component should be located under <body>,
     * but also invalid html containing the hierarchy without <html>, <head> and
     * <body> tags is accepted.
     * 
     * @param html
     *            the html document describing the component design
     * @return the DesignContext created while traversing the tree. The
     *         top-level component of the created component hierarchy can be
     *         accessed using result.getRootComponent(), where result is the
     *         object returned by this method.
     * @throws IOException
     */
    public static DesignContext parse(String html) {
        Document doc = Jsoup.parse(html);
        return parse(doc);
    }

    /**
     * Constructs a component hierarchy from the design specified as an html
     * tree.
     * 
     */
    private static DesignContext parse(Document doc) {
        DesignContext designContext = new DesignContext(doc);
        designContext.getPrefixes(doc);
        // No special handling for a document without a body element - should be
        // taken care of by jsoup.
        Element root = doc.body();
        DesignSynchronizable componentRoot = null;
        for (Node element : root.childNodes()) {
            if (element instanceof Element) {
                if (componentRoot != null) {
                    throw new DesignException(
                            "The first level of a component hierarchy should contain a single root component, but found "
                                    + "two: "
                                    + componentRoot
                                    + " and "
                                    + element + ".");
                }
                // createChild creates the entire component hierarchy
                componentRoot = designContext.createChild((Element) element);
                designContext.setComponentRoot(componentRoot);
            }
        }
        return designContext;
    }

    /**
     * Generates an html tree representation representing the component
     * hierarchy having the given root. The hierarchy is stored under <body> in
     * the tree. The generated tree corresponds to a valid html document.
     * 
     * 
     * @param root
     *            the root of the component hierarchy
     * @return an html tree representation of the component hierarchy
     */
    public static Document createHtml(DesignContext designContext) {
        // Create the html tree skeleton.
        Document doc = new Document("");
        DocumentType docType = new DocumentType("html", "", "", "");
        doc.appendChild(docType);
        Element html = doc.createElement("html");
        doc.appendChild(html);
        html.appendChild(doc.createElement("head"));
        Element body = doc.createElement("body");
        html.appendChild(body);
        designContext.storePrefixes(doc);

        // Append the design under <body> in the html tree. createNode
        // creates the entire component hierarchy rooted at the
        // given root node.
        DesignSynchronizable root = designContext.getComponentRoot();
        Node rootNode = designContext.createNode(root);
        body.appendChild(rootNode);
        return doc;
    }

    /**
     * Generates an html file corresponding to the component hierarchy with the
     * given root.
     * 
     * @param writer
     * @param root
     * @throws IOException
     */
    public static void createHtml(BufferedWriter writer, DesignContext ctx)
            throws IOException {
        String docAsString = createHtml(ctx).toString();
        writer.write(docAsString);
    }
}
