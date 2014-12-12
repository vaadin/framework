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

import java.beans.IntrospectionException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.DesignContext.ComponentCreatedEvent;
import com.vaadin.ui.declarative.DesignContext.ComponentCreationListener;

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
public class LayoutHandler implements Serializable {
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
        return parse(doc, null);
    }

    /**
     * Constructs a component hierarchy from the design specified as an html
     * document. The component hierarchy must contain exactly one top-level
     * Component. The component should be located under <body>, but also invalid
     * html containing the hierarchy without <html>, <head> and <body> tags is
     * accepted. You can optionally pass instance for the root component with
     * some uninitialized instance fields. The fields will be automatically
     * populated when parsing the design based on the component ids, local ids,
     * and captions of the components in the design.
     * 
     * @param html
     *            the html document describing the component design
     * @param rootInstance
     *            the root instance with fields to be mapped to components in
     *            the design
     * @return the DesignContext created while traversing the tree. The
     *         top-level component of the created component hierarchy can be
     *         accessed using result.getRootComponent(), where result is the
     *         object returned by this method.
     * @throws IOException
     */
    public static DesignContext parse(InputStream html, Component rootInstance) {
        Document doc;
        try {
            doc = Jsoup.parse(html, "UTF-8", "", Parser.htmlParser());
        } catch (IOException e) {
            throw new DesignException("The html document cannot be parsed.");
        }
        return parse(doc, rootInstance);
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
        return parse(doc, null);
    }

    /**
     * Constructs a component hierarchy from the design specified as an html
     * document given as a string. The component hierarchy must contain exactly
     * one top-level Component. The component should be located under <body>,
     * but also invalid html containing the hierarchy without <html>, <head> and
     * <body> tags is accepted. You can optionally pass instance for the root
     * component with some uninitialized instance fields. The fields will be
     * automatically populated when parsing the design based on the component
     * ids, local ids, and captions of the components in the design.
     * 
     * @param html
     *            the html document describing the component design
     * @param rootInstance
     *            the root instance with fields to be mapped to components in
     *            the design
     * @return the DesignContext created while traversing the tree. The
     *         top-level component of the created component hierarchy can be
     *         accessed using result.getRootComponent(), where result is the
     *         object returned by this method.
     * @throws IOException
     */
    public static DesignContext parse(String html, Component rootInstance) {
        Document doc = Jsoup.parse(html);
        return parse(doc, rootInstance);
    }

    /**
     * Constructs a component hierarchy from the design specified as an html
     * tree. If componentRoot is not null, the component instances created
     * during synchronizing the design are assigned to its member fields based
     * on their id, localId, and caption
     * 
     * @param doc
     *            the html tree
     * @param componentRoot
     *            optional component root instance with some member fields. The
     *            type must match the type of the root element in the design.
     *            The member fields whose type is assignable from
     *            {@link Component} are set when parsing the component tree
     * 
     */
    private static DesignContext parse(Document doc, Component componentRoot) {
        DesignContext designContext = new DesignContext(doc);
        designContext.getPrefixes(doc);
        // No special handling for a document without a body element - should be
        // taken care of by jsoup.
        Element root = doc.body();
        Elements children = root.children();
        if (children.size() != 1) {
            throw new DesignException(
                    "The first level of a component hierarchy should contain exactly one root component, but found "
                            + children.size());
        }
        Element element = children.first();
        if (componentRoot != null) {
            // user has specified root instance that may have member fields that
            // should be bound
            FieldBinder binder = null;
            try {
                binder = new FieldBinder(componentRoot);
            } catch (IntrospectionException e) {
                throw new DesignException(
                        "Could not bind fields of the root component", e);
            }
            final FieldBinder fBinder = binder;
            // create listener for component creations that binds the created
            // components to the componentRoot instance fields
            ComponentCreationListener creationListener = new ComponentCreationListener() {
                @Override
                public void componentCreated(ComponentCreatedEvent event) {
                    fBinder.bindField(event.getComponent(), event.getLocalId());
                }
            };
            designContext.addComponentCreationListener(creationListener);
            // create subtree
            designContext.synchronizeAndRegister(componentRoot, element);
            // make sure that all the member fields are bound
            Collection<String> unboundFields = binder.getUnboundFields();
            if (!unboundFields.isEmpty()) {
                throw new DesignException(
                        "Found unbound fields from component root "
                                + unboundFields);
            }
            // no need to listen anymore
            designContext.removeComponentCreationListener(creationListener);
        } else {
            // createChild creates the entire component hierarchy
            componentRoot = designContext.createChild(element);
        }
        designContext.setComponentRoot(componentRoot);
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
        Component root = designContext.getComponentRoot();
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
