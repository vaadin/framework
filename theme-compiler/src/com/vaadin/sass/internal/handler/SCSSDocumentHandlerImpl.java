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

package com.vaadin.sass.internal.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.SelectorList;

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.parser.LexicalUnitImpl;
import com.vaadin.sass.internal.tree.BlockNode;
import com.vaadin.sass.internal.tree.CommentNode;
import com.vaadin.sass.internal.tree.ContentNode;
import com.vaadin.sass.internal.tree.ExtendNode;
import com.vaadin.sass.internal.tree.FontFaceNode;
import com.vaadin.sass.internal.tree.ForNode;
import com.vaadin.sass.internal.tree.ImportNode;
import com.vaadin.sass.internal.tree.KeyframeSelectorNode;
import com.vaadin.sass.internal.tree.KeyframesNode;
import com.vaadin.sass.internal.tree.ListAppendNode;
import com.vaadin.sass.internal.tree.ListContainsNode;
import com.vaadin.sass.internal.tree.ListRemoveNode;
import com.vaadin.sass.internal.tree.MediaNode;
import com.vaadin.sass.internal.tree.MicrosoftRuleNode;
import com.vaadin.sass.internal.tree.MixinDefNode;
import com.vaadin.sass.internal.tree.MixinNode;
import com.vaadin.sass.internal.tree.NestPropertiesNode;
import com.vaadin.sass.internal.tree.Node;
import com.vaadin.sass.internal.tree.RuleNode;
import com.vaadin.sass.internal.tree.SimpleNode;
import com.vaadin.sass.internal.tree.VariableNode;
import com.vaadin.sass.internal.tree.WhileNode;
import com.vaadin.sass.internal.tree.controldirective.EachDefNode;
import com.vaadin.sass.internal.tree.controldirective.ElseNode;
import com.vaadin.sass.internal.tree.controldirective.IfElseDefNode;
import com.vaadin.sass.internal.tree.controldirective.IfNode;

public class SCSSDocumentHandlerImpl implements SCSSDocumentHandler {

    private final ScssStylesheet styleSheet;
    Stack<Node> nodeStack = new Stack<Node>();

    public SCSSDocumentHandlerImpl() {
        this(new ScssStylesheet());
    }

    public SCSSDocumentHandlerImpl(ScssStylesheet styleSheet) {
        this.styleSheet = styleSheet;
        nodeStack.push(styleSheet);
    }

    @Override
    public ScssStylesheet getStyleSheet() {
        return styleSheet;
    }

    @Override
    public void startDocument(InputSource source) throws CSSException {
        nodeStack.push(styleSheet);
    }

    @Override
    public void endDocument(InputSource source) throws CSSException {
    }

    @Override
    public void variable(String name, LexicalUnitImpl value, boolean guarded) {
        VariableNode node = new VariableNode(name, value, guarded);
        nodeStack.peek().appendChild(node);
    }

    @Override
    public void debugDirective() {
    }

    @Override
    public ForNode forDirective(String var, String from, String to,
            boolean exclusive, String body) {
        ForNode node = new ForNode(var, from, to, exclusive, body);
        log(node);
        return node;
    }

    @Override
    public EachDefNode startEachDirective(String var, ArrayList<String> list) {
        EachDefNode node = new EachDefNode(var, list);
        nodeStack.peek().appendChild(node);
        nodeStack.push(node);
        return node;
    }

    @Override
    public EachDefNode startEachDirective(String var, String listVariable) {
        EachDefNode node = new EachDefNode(var, listVariable);
        nodeStack.peek().appendChild(node);
        nodeStack.push(node);
        return node;
    }

    @Override
    public void endEachDirective() {
        nodeStack.pop();
    }

    @Override
    public WhileNode whileDirective(String condition, String body) {
        WhileNode node = new WhileNode(condition, body);
        log(node);
        return node;
    }

    @Override
    public void comment(String text) throws CSSException {
        CommentNode node = new CommentNode(text);
        nodeStack.peek().appendChild(node);
    }

    @Override
    public void ignorableAtRule(String atRule) throws CSSException {
        log("ignorableAtRule(String atRule): " + atRule);
    }

    @Override
    public void namespaceDeclaration(String prefix, String uri)
            throws CSSException {
        log("namespaceDeclaration(String prefix, String uri): " + prefix + ", "
                + uri);
    }

    @Override
    public void importStyle(String uri, SACMediaList media,
            String defaultNamespaceURI) throws CSSException {
    }

    @Override
    public void startMedia(SACMediaList media) throws CSSException {
        MediaNode node = new MediaNode(media);
        nodeStack.peek().appendChild(node);
        nodeStack.push(node);
    }

    @Override
    public void endMedia(SACMediaList media) throws CSSException {
        nodeStack.pop();
    }

    @Override
    public void startPage(String name, String pseudo_page) throws CSSException {
        log("startPage(String name, String pseudo_page): " + name + ", "
                + pseudo_page);
    }

    @Override
    public void endPage(String name, String pseudo_page) throws CSSException {
        log("endPage(String name, String pseudo_page): " + name + ", "
                + pseudo_page);
    }

    @Override
    public void startFontFace() throws CSSException {
        FontFaceNode node = new FontFaceNode();
        nodeStack.peek().appendChild(node);
        nodeStack.push(node);
    }

    @Override
    public void endFontFace() throws CSSException {
        nodeStack.pop();
    }

    @Override
    public void startSelector(ArrayList<String> selectors) throws CSSException {
        BlockNode node = new BlockNode(selectors);
        nodeStack.peek().appendChild(node);
        nodeStack.push(node);
    }

    @Override
    public void endSelector() throws CSSException {
        nodeStack.pop();
    }

    @Override
    public void property(String name, LexicalUnit value, boolean important)
            throws CSSException {
        property(name, (LexicalUnitImpl) value, important, null);
    }

    @Override
    public void property(String name, LexicalUnitImpl value, boolean important,
            String comment) {
        RuleNode node = new RuleNode(name, value, important, comment);
        nodeStack.peek().appendChild(node);
    }

    @Override
    public void extendDirective(ArrayList<String> list) {
        ExtendNode node = new ExtendNode(list);
        nodeStack.peek().appendChild(node);
    }

    @Override
    public void startNestedProperties(String name) {
        NestPropertiesNode node = new NestPropertiesNode(name);
        nodeStack.peek().appendChild(node);
        nodeStack.push(node);
    }

    @Override
    public void endNestedProperties(String name) {
        nodeStack.pop();
    }

    @Override
    public void startMixinDirective(String name, Collection<VariableNode> args) {
        MixinDefNode node = new MixinDefNode(name.trim(), args);
        nodeStack.peek().appendChild(node);
        nodeStack.push(node);
    }

    @Override
    public void endMixinDirective(String name, Collection<VariableNode> args) {
        nodeStack.pop();
    }

    @Override
    public void importStyle(String uri, SACMediaList media, boolean isURL) {
        ImportNode node = new ImportNode(uri, media, isURL);
        nodeStack.peek().appendChild(node);
    }

    @Override
    public void startIfElseDirective() {
        final IfElseDefNode node = new IfElseDefNode();
        nodeStack.peek().appendChild(node);
        nodeStack.push(node);
    }

    @Override
    public void ifDirective(String evaluator) {
        if (nodeStack.peek() instanceof IfNode) {
            nodeStack.pop();
        }
        IfNode node = new IfNode(evaluator);
        nodeStack.peek().appendChild(node);
        nodeStack.push(node);
    }

    @Override
    public void elseDirective() {
        if (nodeStack.peek() instanceof IfNode) {
            nodeStack.pop();
        }
        ElseNode node = new ElseNode();
        nodeStack.peek().appendChild(node);
        nodeStack.push(node);
    }

    @Override
    public void endIfElseDirective() {
        if ((nodeStack.peek() instanceof ElseNode)
                || (nodeStack.peek() instanceof IfNode)) {
            nodeStack.pop();
        }
        nodeStack.pop();
    }

    @Override
    public void microsoftDirective(String name, String value) {
        MicrosoftRuleNode node = new MicrosoftRuleNode(name, value);
        nodeStack.peek().appendChild(node);
    }

    // rule that is passed to the output as-is (except variable value
    // substitution) - no children
    public void unrecognizedRule(String text) {
        SimpleNode node = new SimpleNode(text);
        nodeStack.peek().appendChild(node);
    }

    @Override
    public void endSelector(SelectorList arg0) throws CSSException {
        // TODO Auto-generated method stub

    }

    @Override
    public void startSelector(SelectorList arg0) throws CSSException {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeDirective(String variable, String list, String remove,
            String separator) {
        ListRemoveNode node = new ListRemoveNode(variable, list, remove,
                separator);
        nodeStack.peek().appendChild(node);
    }

    @Override
    public void appendDirective(String variable, String list, String append,
            String separator) {
        ListAppendNode node = new ListAppendNode(variable, list, append,
                separator);
        nodeStack.peek().appendChild(node);
    }

    @Override
    public void containsDirective(String variable, String list,
            String contains, String separator) {
        ListContainsNode node = new ListContainsNode(variable, list, contains,
                separator);
        nodeStack.peek().appendChild(node);
    }

    @Override
    public void startKeyFrames(String keyframeName, String animationName) {
        KeyframesNode node = new KeyframesNode(keyframeName, animationName);
        nodeStack.peek().appendChild(node);
        nodeStack.push(node);

    }

    @Override
    public void endKeyFrames() {
        nodeStack.pop();

    }

    @Override
    public void startKeyframeSelector(String selector) {
        KeyframeSelectorNode node = new KeyframeSelectorNode(selector);
        nodeStack.peek().appendChild(node);
        nodeStack.push(node);

    }

    @Override
    public void endKeyframeSelector() {
        nodeStack.pop();
    }

    @Override
    public void contentDirective() {
        ContentNode node = new ContentNode();
        nodeStack.peek().appendChild(node);
    }

    @Override
    public void startInclude(String name, List<LexicalUnitImpl> args) {
        MixinNode node = new MixinNode(name, args);
        nodeStack.peek().appendChild(node);
        nodeStack.push(node);

    }

    @Override
    public void endInclude() {
        nodeStack.pop();
    }

    private void log(Object object) {
        if (object != null) {
            log(object.toString());
        } else {
            log(null);
        }
    }

    private void log(String msg) {
        Logger.getLogger(SCSSDocumentHandlerImpl.class.getName()).log(
                Level.INFO, msg);
    }
}
