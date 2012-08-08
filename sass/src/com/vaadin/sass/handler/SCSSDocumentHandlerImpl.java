package com.vaadin.sass.handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.SelectorList;

import com.vaadin.sass.ScssStylesheet;
import com.vaadin.sass.tree.BlockNode;
import com.vaadin.sass.tree.EachNode;
import com.vaadin.sass.tree.ExtendNode;
import com.vaadin.sass.tree.ForNode;
import com.vaadin.sass.tree.IfNode;
import com.vaadin.sass.tree.ImportNode;
import com.vaadin.sass.tree.MediaNode;
import com.vaadin.sass.tree.MixinDefNode;
import com.vaadin.sass.tree.MixinNode;
import com.vaadin.sass.tree.NestPropertiesNode;
import com.vaadin.sass.tree.Node;
import com.vaadin.sass.tree.RuleNode;
import com.vaadin.sass.tree.VariableNode;
import com.vaadin.sass.tree.WhileNode;

public class SCSSDocumentHandlerImpl implements SCSSDocumentHandler {

    private final ScssStylesheet styleSheet;
    Stack<Node> nodeStack = new Stack<Node>();
    private Map<String, Stack<LexicalUnit>> variableMap;

    public SCSSDocumentHandlerImpl() {
        this(new ScssStylesheet());
        variableMap = new HashMap<String, Stack<LexicalUnit>>();
    }

    public SCSSDocumentHandlerImpl(ScssStylesheet styleSheet) {
        this.styleSheet = styleSheet;
        nodeStack.push(styleSheet);
    }

    public void addVariable(String varName, LexicalUnit value) {
        if (variableMap.get(varName) == null) {
            variableMap.put(varName, new Stack<LexicalUnit>());
        }
        Stack<LexicalUnit> valueStack = variableMap.get(varName);
        valueStack.push(value);
    }

    public void removeVaraible(String varName) {
        Stack<LexicalUnit> valueStack = variableMap.get(varName);
        if (valueStack != null && !valueStack.isEmpty()) {
            valueStack.pop();
        }
    }

    public LexicalUnit getVariable(String varName) {
        Stack<LexicalUnit> valueStack = variableMap.get(varName);
        if (valueStack != null && !valueStack.isEmpty()) {
            return valueStack.peek();
        }
        return null;
    }

    @Override
    public ScssStylesheet getStyleSheet() {
        return styleSheet;
    }

    @Override
    public void startDocument(InputSource source) throws CSSException {
        nodeStack.push(styleSheet);
        // System.out.println("startDocument(InputSource source): "
        // + source.getURI());
    }

    @Override
    public void endDocument(InputSource source) throws CSSException {
        // System.out.println("endDocument(InputSource source): "
        // + source.getURI());
    }

    @Override
    public void variable(String name, LexicalUnit value, boolean guarded) {
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
        System.out.println(node);
        return node;
    }

    @Override
    public EachNode eachDirective(String var, String list, String body) {
        EachNode node = new EachNode(var, list, body);
        System.out.println(node);
        return node;
    }

    @Override
    public WhileNode whileDirective(String condition, String body) {
        WhileNode node = new WhileNode(condition, body);
        System.out.println(node);
        return node;
    }

    @Override
    public IfNode ifDirective() {
        return new IfNode();
    }

    @Override
    public void comment(String text) throws CSSException {
        System.out.println("comment(String text): " + text);
    }

    @Override
    public void ignorableAtRule(String atRule) throws CSSException {
        System.out.println("ignorableAtRule(String atRule): " + atRule);
    }

    @Override
    public void namespaceDeclaration(String prefix, String uri)
            throws CSSException {
        System.out.println("namespaceDeclaration(String prefix, String uri): "
                + prefix + ", " + uri);
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
        System.out.println("startPage(String name, String pseudo_page): "
                + name + ", " + pseudo_page);
    }

    @Override
    public void endPage(String name, String pseudo_page) throws CSSException {
        System.out.println("endPage(String name, String pseudo_page): " + name
                + ", " + pseudo_page);
    }

    @Override
    public void startFontFace() throws CSSException {
        System.out.println("startFontFace()");
    }

    @Override
    public void endFontFace() throws CSSException {
        System.out.println("endFontFace()");
    }

    @Override
    public void startSelector(SelectorList selectors) throws CSSException {
        BlockNode node = new BlockNode(selectors);
        nodeStack.peek().appendChild(node);
        nodeStack.push(node);
    }

    @Override
    public void endSelector(SelectorList selectors) throws CSSException {
        nodeStack.pop();
    }

    @Override
    public void property(String name, LexicalUnit value, boolean important)
            throws CSSException {
        property(name, value, important, null);
    }

    public void property(String name, LexicalUnit value, boolean important,
            String comment) {
        RuleNode node = new RuleNode(name, value, important, comment);
        nodeStack.peek().appendChild(node);
    }

    @Override
    public void extendDirective(SelectorList list) {
        ExtendNode node = new ExtendNode(list);
        nodeStack.peek().appendChild(node);
    }

    @Override
    public MixinDefNode mixinDirective(String name, String args, String body) {
        MixinDefNode node = new MixinDefNode(name, args, body);
        return node;
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
        MixinDefNode node = new MixinDefNode(name, args);
        nodeStack.peek().appendChild(node);
        nodeStack.push(node);
    }

    @Override
    public void endMixinDirective(String name, Collection<VariableNode> args) {
        nodeStack.pop();
    }

    @Override
    public void includeDirective(String name, Collection<LexicalUnit> args) {
        MixinNode node = new MixinNode(name, args);
        nodeStack.peek().appendChild(node);
    }

    @Override
    public void importStyle(String uri, SACMediaList media, boolean isURL) {
        ImportNode node = new ImportNode(uri, media, isURL);
        nodeStack.peek().appendChild(node);
    }
}
