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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.sass.internal.visitor.ExtendNodeHandler;

public class ExtendNode extends Node implements IVariableNode {
    private static final long serialVersionUID = 3301805078983796878L;

    ArrayList<String> list;

    public ExtendNode(ArrayList<String> list) {
        super();
        this.list = list;
    }

    public ArrayList<String> getList() {
        return list;
    }

    @Override
    public void replaceVariables(ArrayList<VariableNode> variables) {

    }

    public String getListAsString() {
        StringBuilder b = new StringBuilder();
        for (final String s : list) {
            b.append(s);
        }

        return b.toString();
    }

    @Override
    public void traverse() {
        try {
            ExtendNodeHandler.traverse(this);
            getParentNode().removeChild(this);
        } catch (Exception e) {
            Logger.getLogger(ExtendNode.class.getName()).log(Level.SEVERE,
                    null, e);
        }
    }
}
