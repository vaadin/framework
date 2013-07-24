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

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.SACMediaList;

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.parser.LexicalUnitImpl;
import com.vaadin.sass.internal.tree.ForNode;
import com.vaadin.sass.internal.tree.VariableNode;
import com.vaadin.sass.internal.tree.WhileNode;
import com.vaadin.sass.internal.tree.controldirective.EachDefNode;

public interface SCSSDocumentHandler extends DocumentHandler {
    ScssStylesheet getStyleSheet();

    void variable(String name, LexicalUnitImpl value, boolean guarded);

    void startMixinDirective(String name, Collection<VariableNode> args);

    void endMixinDirective(String name, Collection<VariableNode> args);

    void startFunctionDirective(String name, Collection<VariableNode> args);

    void endFunctionDirective(String name, Collection<VariableNode> args);

    void debugDirective();

    void startReturnDirective();

    void endReturnDirective();

    void startNestedProperties(String name);

    void endNestedProperties(String name);

    void includeDirective(String name, Collection<LexicalUnitImpl> args);

    void importStyle(String uri, SACMediaList media, boolean isURL);

    void property(String name, LexicalUnitImpl value, boolean important,
            String comment);

    void startForDirective(String var, LexicalUnitImpl from, 
            LexicalUnitImpl to, boolean inclusive);

    void endForDirective();

    void startEachDirective(String variable, ArrayList<String> list);

    void endEachDirective();

    void startWhileDirective();

    void endWhileDirective();

    void whileDirective(String evaluator);

    void startIfElseDirective();

    void endIfElseDirective();

    void ifDirective(String evaluator);

    void elseDirective();

    void startSelector(ArrayList<String> selectors) throws CSSException;

    void endSelector() throws CSSException;

    void extendDirective(ArrayList<String> list);

    void microsoftDirective(String name, String value);

    void startEachDirective(String var, String listVariable);

    void removeDirective(String variable, String list, String remove,
            String separator);

    void appendDirective(String variable, String list, String remove,
            String separator);

    void containsDirective(String variable, String list, String contains,
            String separator);

    void startKeyFrames(String keyframeName, String animationname);

    void endKeyFrames();

    void startKeyframeSelector(String selector);

    void endKeyframeSelector();

    void contentDirective();

    void startIncludeContentBlock(String name);

    void endIncludeContentBlock();

}
