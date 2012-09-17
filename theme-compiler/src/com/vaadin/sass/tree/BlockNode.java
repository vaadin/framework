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

package com.vaadin.sass.tree;

import java.util.ArrayList;

import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;

import com.vaadin.sass.parser.SelectorListImpl;
import com.vaadin.sass.selector.SelectorUtil;
import com.vaadin.sass.util.Clonable;
import com.vaadin.sass.util.DeepCopy;

public class BlockNode extends Node implements Clonable, IVariableNode {

    private static final long serialVersionUID = 5742962631468325048L;

    SelectorList selectorList;

    public BlockNode(SelectorList selectorList) {
        this.selectorList = selectorList;
    }

    public SelectorList getSelectorList() {
        return selectorList;
    }

    public void setSelectorList(SelectorList selectorList) {
        this.selectorList = selectorList;
    }

    public String toString(boolean indent) {
        StringBuilder string = new StringBuilder();
        string.append(SelectorUtil.toString(selectorList));
        string.append(" {\n");
        for (Node child : children) {
            if (indent) {
                string.append("\t");
            }
            string.append("\t" + child.toString() + "\n");
        }
        if (indent) {
            string.append("\t");
        }
        string.append("}");
        return string.toString();
    }

    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {

        SelectorListImpl clonedSelectorList = null;

        if (selectorList != null) {
            clonedSelectorList = new SelectorListImpl();
            for (int i = 0; i < selectorList.getLength(); i++) {
                clonedSelectorList.addSelector(selectorList.item(i));
            }
        }
        final BlockNode clone = new BlockNode(clonedSelectorList);
        for (Node child : getChildren()) {
            clone.getChildren().add((Node) DeepCopy.copy(child));
        }
        return clone;
    }

    @Override
    public void replaceVariables(ArrayList<VariableNode> variables) {
        SelectorListImpl newList = new SelectorListImpl();

        if (selectorList != null) {
            for (int i = 0; i < selectorList.getLength(); i++) {
                Selector selector = selectorList.item(i);

                for (final VariableNode node : variables) {

                    if (SelectorUtil.toString(selector)
                            .contains(node.getName())) {
                        try {
                            selector = SelectorUtil
                                    .createSelectorAndreplaceSelectorVariableWithValue(
                                            selector, node.getName(), node
                                                    .getExpr().toString());
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }
                newList.addSelector(selector);
            }

            selectorList = newList;
        }
    }

}
