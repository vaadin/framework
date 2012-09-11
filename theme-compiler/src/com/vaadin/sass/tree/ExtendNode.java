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

public class ExtendNode extends Node implements IVariableNode {
    private static final long serialVersionUID = 3301805078983796878L;

    SelectorList list;

    public ExtendNode(SelectorList list) {
        super();
        this.list = list;
    }

    public SelectorList getList() {
        return list;
    }

    @Override
    public void replaceVariables(ArrayList<VariableNode> variables) {
        SelectorListImpl newList = new SelectorListImpl();

        for (int i = 0; i < list.getLength(); i++) {
            Selector selector = list.item(i);

            for (final VariableNode node : variables) {

                if (SelectorUtil.toString(selector).contains(node.getName())) {
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

        list = newList;
    }

}
