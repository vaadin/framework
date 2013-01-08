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
package com.vaadin.sass.internal.tree;

import java.util.ArrayList;

import com.vaadin.sass.internal.parser.LexicalUnitImpl;

public class ListContainsNode extends ListModifyNode {

    public ListContainsNode(String variable, String list, String contains,
            String separator) {
        this.variable = variable;
        checkSeparator(separator, list);
        populateList(list, contains);
    }

    @Override
    protected void modifyList(ArrayList<String> newList) {
        // Does not actually modify the list
    }

    @Override
    public VariableNode getModifiedList() {
        String contains = "" + list.containsAll(modify);
        VariableNode node = new VariableNode(variable.substring(1),
                LexicalUnitImpl.createString(contains), false);
        return node;

    }
}
