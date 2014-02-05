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

package com.vaadin.sass.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;

import com.vaadin.buildhelpers.CompileTheme;
import com.vaadin.sass.internal.handler.SCSSDocumentHandler;
import com.vaadin.sass.internal.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.internal.handler.SCSSErrorHandler;
import com.vaadin.sass.internal.parser.ParseException;
import com.vaadin.sass.internal.parser.Parser;
import com.vaadin.sass.internal.parser.SCSSParseException;
import com.vaadin.sass.internal.resolver.ClassloaderResolver;
import com.vaadin.sass.internal.resolver.FilesystemResolver;
import com.vaadin.sass.internal.resolver.ScssStylesheetResolver;
import com.vaadin.sass.internal.tree.BlockNode;
import com.vaadin.sass.internal.tree.MixinDefNode;
import com.vaadin.sass.internal.tree.Node;
import com.vaadin.sass.internal.tree.VariableNode;
import com.vaadin.sass.internal.tree.controldirective.IfElseDefNode;
import com.vaadin.sass.internal.visitor.ExtendNodeHandler;
import com.vaadin.sass.internal.visitor.ImportNodeHandler;

public class ScssStylesheet extends Node {

    private static final long serialVersionUID = 3849790204404961608L;

    private static ScssStylesheet mainStyleSheet = null;

    private static final HashMap<String, VariableNode> variables = new HashMap<String, VariableNode>();

    private static final Map<String, MixinDefNode> mixinDefs = new HashMap<String, MixinDefNode>();

    private static final HashSet<IfElseDefNode> ifElseDefNodes = new HashSet<IfElseDefNode>();

    private static HashMap<Node, Node> lastNodeAdded = new HashMap<Node, Node>();

    private File file;

    private String charset;

    private List<ScssStylesheetResolver> resolvers = new ArrayList<ScssStylesheetResolver>();

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
     * Main entry point for the SASS compiler. Takes in a file and builds up a
     * ScssStylesheet tree out of it. Calling compile() on it will transform
     * SASS into CSS. Calling toString() will print out the SCSS/CSS.
     * 
     * @param identifier
     *            The file path. If null then null is returned.
     * @return
     * @throws CSSException
     * @throws IOException
     */
    public static ScssStylesheet get(String identifier) throws CSSException,
            IOException {
        return get(identifier, null);
    }

    /**
     * Main entry point for the SASS compiler. Takes in a file and an optional
     * parent style sheet, then builds up a ScssStylesheet tree out of it.
     * Calling compile() on it will transform SASS into CSS. Calling toString()
     * will print out the SCSS/CSS.
     * 
     * @param identifier
     *            The file path. If null then null is returned.
     * @param parentStylesheet
     *            Style sheet from which to inherit resolvers and encoding. May
     *            be null.
     * @return
     * @throws CSSException
     * @throws IOException
     */
    public static ScssStylesheet get(String identifier,
            ScssStylesheet parentStylesheet) throws CSSException, IOException {
        return get(identifier, parentStylesheet, new SCSSDocumentHandlerImpl(),
                new SCSSErrorHandler());
    }

    /**
     * Main entry point for the SASS compiler. Takes in a file, an optional
     * parent stylesheet, and document and error handlers. Then builds up a
     * ScssStylesheet tree out of it. Calling compile() on it will transform
     * SASS into CSS. Calling toString() will print out the SCSS/CSS.
     * 
     * @param identifier
     *            The file path. If null then null is returned.
     * @param parentStylesheet
     *            Style sheet from which to inherit resolvers and encoding. May
     *            be null.
     * @param documentHandler
     *            Instance of document handler. May not be null.
     * @param errorHandler
     *            Instance of error handler. May not be null.
     * @return
     * @throws CSSException
     * @throws IOException
     */
    public static ScssStylesheet get(String identifier,
            ScssStylesheet parentStylesheet,
            SCSSDocumentHandler documentHandler, SCSSErrorHandler errorHandler)
            throws CSSException, IOException {
        /*
         * The encoding to be used is passed through "encoding" parameter. the
         * imported children scss node will have the same encoding as their
         * parent, ultimately the root scss file. The root scss node has this
         * "encoding" parameter to be null. Its encoding is determined by the
         * 
         * @charset declaration, the default one is ASCII.
         */

        if (identifier == null) {
            return null;
        }

        // FIXME Is this actually intended? /John 1.3.2013
        File file = new File(identifier);
        file = file.getCanonicalFile();

        ScssStylesheet stylesheet = documentHandler.getStyleSheet();
        if (parentStylesheet == null) {
            // Use default resolvers
            stylesheet.addResolver(new FilesystemResolver());
            stylesheet.addResolver(new ClassloaderResolver());
        } else {
            // Use parent resolvers
            stylesheet.setResolvers(parentStylesheet.getResolvers());
        }
        InputSource source = stylesheet.resolveStylesheet(identifier,
                parentStylesheet);
        if (source == null) {
            return null;
        }
        if (parentStylesheet != null) {
            source.setEncoding(parentStylesheet.getCharset());
        }

        Parser parser = new Parser();
        parser.setErrorHandler(errorHandler);
        parser.setDocumentHandler(documentHandler);

        try {
            parser.parseStyleSheet(source);
        } catch (ParseException e) {
            // catch ParseException, re-throw a SCSSParseException which has
            // file name info.
            throw new SCSSParseException(e, identifier);
        }

        stylesheet.setCharset(parser.getInputSource().getEncoding());
        return stylesheet;
    }

    public InputSource resolveStylesheet(String identifier,
            ScssStylesheet parentStylesheet) {
        for (ScssStylesheetResolver resolver : getResolvers()) {
            InputSource source = resolver.resolve(parentStylesheet, identifier);
            if (source != null) {
                File f = new File(source.getURI());
                setFile(f);
                return source;
            }
        }

        return null;
    }

    /**
     * Retrieves a list of resolvers to use when resolving imports
     * 
     * @since 7.2
     * @return the resolvers used to resolving imports
     */
    public List<ScssStylesheetResolver> getResolvers() {
        return Collections.unmodifiableList(resolvers);
    }

    /**
     * Sets the list of resolvers to use when resolving imports
     * 
     * @since 7.2
     * @param resolvers
     *            the resolvers to set
     */
    public void setResolvers(List<ScssStylesheetResolver> resolvers) {
        this.resolvers = new ArrayList<ScssStylesheetResolver>(resolvers);
    }

    /**
     * Adds the given resolver to the resolver list
     * 
     * @since 7.2
     * @param resolver
     *            The resolver to add
     */
    public void addResolver(ScssStylesheetResolver resolver) {
        resolvers.add(resolver);
    }

    /**
     * Applies all the visitors and compiles SCSS into Css.
     * 
     * @throws Exception
     */
    public void compile() throws Exception {
        mainStyleSheet = this;
        mixinDefs.clear();
        variables.clear();
        ifElseDefNodes.clear();
        lastNodeAdded.clear();
        ExtendNodeHandler.clear();
        importOtherFiles(this);
        populateDefinitions(this);
        traverse(this);
        removeEmptyBlocks(this);
    }

    private void importOtherFiles(ScssStylesheet node) {
        ImportNodeHandler.traverse(node);
    }

    private void populateDefinitions(Node node) {
        if (node instanceof MixinDefNode) {
            mixinDefs.put(((MixinDefNode) node).getName(), (MixinDefNode) node);
            node.getParentNode().removeChild(node);
        } else if (node instanceof IfElseDefNode) {
            ifElseDefNodes.add((IfElseDefNode) node);
        }

        for (final Node child : new ArrayList<Node>(node.getChildren())) {
            populateDefinitions(child);
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
        String delimeter = "\n\n";
        // add charset declaration, if it is not default "ASCII".
        if (!"ASCII".equals(getCharset())) {
            string.append("@charset \"").append(getCharset()).append("\";")
                    .append(delimeter);
        }
        if (children.size() > 0) {
            string.append(children.get(0).toString());
        }
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

    public static ScssStylesheet get() {
        return mainStyleSheet;
    }

    @Override
    public void traverse() {
        // Not used for ScssStylesheet
    }

    /**
     * Traverses a node and its children recursively, calling all the
     * appropriate handlers via {@link Node#traverse()}.
     * 
     * The node itself may be removed during the traversal and replaced with
     * other nodes at the same position or later on the child list of its
     * parent.
     * 
     * @param node
     *            node to traverse
     * @return true if the node was removed (and possibly replaced by others),
     *         false if not
     */
    public boolean traverse(Node node) {
        Node originalParent = node.getParentNode();

        node.traverse();

        Map<String, VariableNode> variableScope = openVariableScope();

        // the size of the child list may change on each iteration: current node
        // may get deleted and possibly other nodes have been inserted where it
        // was or after that position
        for (int i = 0; i < node.getChildren().size(); i++) {
            Node current = node.getChildren().get(i);
            if (traverse(current)) {
                // current has been removed
                --i;
            }
        }

        closeVariableScope(variableScope);

        // clean up insert point so that processing of the next block will
        // insert after that block
        lastNodeAdded.remove(originalParent);

        // has the node been removed from its parent?
        if (originalParent != null) {
            boolean removed = !originalParent.getChildren().contains(node);
            return removed;
        } else {
            return false;
        }
    }

    /**
     * Start a new scope for variables. Any variables set or modified after
     * opening a new scope are only valid until the scope is closed, at which
     * time they are replaced with their old values.
     * 
     * @return old scope to give to a paired {@link #closeVariableScope(Map)}
     *         call at the end of the scope (unmodifiable map).
     */
    public static Map<String, VariableNode> openVariableScope() {
        @SuppressWarnings("unchecked")
        HashMap<String, VariableNode> variableScope = (HashMap<String, VariableNode>) variables
                .clone();
        return Collections.unmodifiableMap(variableScope);
    }

    /**
     * End a scope for variables, replacing all active variables with those from
     * the original scope (obtained from {@link #openVariableScope()}).
     * 
     * @param originalScope
     *            original scope
     */
    public static void closeVariableScope(
            Map<String, VariableNode> originalScope) {
        variables.clear();
        variables.putAll(originalScope);
    }

    public void removeEmptyBlocks(Node node) {
        // depth first for avoiding re-checking parents of removed nodes
        for (Node child : new ArrayList<Node>(node.getChildren())) {
            removeEmptyBlocks(child);
        }
        Node parent = node.getParentNode();
        if (node instanceof BlockNode && node.getChildren().isEmpty()
                && parent != null) {
            // remove empty block
            parent.removeChild(node);
        }
    }

    public static void addVariable(VariableNode node) {
        variables.put(node.getName(), node);
    }

    public static VariableNode getVariable(String string) {
        return variables.get(string);
    }

    public static ArrayList<VariableNode> getVariables() {
        return new ArrayList<VariableNode>(variables.values());
    }

    public static MixinDefNode getMixinDefinition(String name) {
        return mixinDefs.get(name);
    }

    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Returns the directory containing this style sheet
     * 
     * @since 7.2
     * @return The directory containing this style sheet
     */
    public String getDirectory() {
        return file.getParent();
    }

    /**
     * Returns the full file name for this style sheet
     * 
     * @since 7.2
     * @return The full file name for this style sheet
     */
    public String getFileName() {
        return file.getPath();
    }

    public static HashMap<Node, Node> getLastNodeAdded() {
        return lastNodeAdded;
    }

    public static final void warning(String msg) {
        Logger.getLogger(ScssStylesheet.class.getName()).warning(msg);
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    static {
        String logFile = System.getProperty("java.util.logging.config.file");
        if (logFile == null) {
            try {
                LogManager.getLogManager().readConfiguration(
                        CompileTheme.class
                                .getResourceAsStream("/logging.properties"));
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
