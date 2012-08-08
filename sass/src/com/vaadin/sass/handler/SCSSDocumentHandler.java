package com.vaadin.sass.handler;

import java.util.Collection;

import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.SelectorList;

import com.vaadin.sass.ScssStylesheet;
import com.vaadin.sass.tree.EachNode;
import com.vaadin.sass.tree.ForNode;
import com.vaadin.sass.tree.IfNode;
import com.vaadin.sass.tree.MixinDefNode;
import com.vaadin.sass.tree.VariableNode;
import com.vaadin.sass.tree.WhileNode;

public interface SCSSDocumentHandler extends DocumentHandler {
    ScssStylesheet getStyleSheet();

    void variable(String name, LexicalUnit value, boolean guarded);

    void startMixinDirective(String name, Collection<VariableNode> args);

    void endMixinDirective(String name, Collection<VariableNode> args);

    MixinDefNode mixinDirective(String name, String args, String body);

    void debugDirective();

    ForNode forDirective(String var, String from, String to, boolean exclusive,
            String body);

    EachNode eachDirective(String var, String list, String body);

    WhileNode whileDirective(String condition, String body);

    IfNode ifDirective();

    void extendDirective(SelectorList list);

    void startNestedProperties(String name);

    void endNestedProperties(String name);

    void includeDirective(String name, Collection<LexicalUnit> args);

    void importStyle(String uri, SACMediaList media, boolean isURL);

    void property(String name, LexicalUnit value, boolean important,
            String comment);
}
