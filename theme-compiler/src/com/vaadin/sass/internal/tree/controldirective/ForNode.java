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

package com.vaadin.sass.internal.tree.controldirective;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.sass.internal.parser.LexicalUnitImpl;
import com.vaadin.sass.internal.tree.IVariableNode;
import com.vaadin.sass.internal.tree.Node;
import com.vaadin.sass.internal.tree.VariableNode;
import com.vaadin.sass.internal.visitor.ForNodeHandler;

/**
 * @version $Revision: 1.0 $
 * @author James Lefeu @ Liferay, Inc.
 */
public class ForNode extends Node implements IVariableNode {
    private static final long serialVersionUID = -1159180539216623335L;

    String var;
    LexicalUnitImpl from;
    LexicalUnitImpl to;
    boolean inclusive;

    public ForNode(String var, LexicalUnitImpl from, 
        LexicalUnitImpl to, boolean inclusive) {

        super();
        this.var = var;
        this.from = from;
        this.to = to;
        this.inclusive = inclusive;
    }

    public String getVariableName() {
        return var;
    }

    public LexicalUnitImpl getFrom() {
        return from;
    }

    public LexicalUnitImpl getTo() {
        return to;
    }

    public boolean getInclusive() {
        return inclusive;
    }

    @Override
    public String toString() {
        return "For Node: " + "{variable: " + var + ", from:" + from + ", to: "
                + to + ", inclusive: " + inclusive;
    }

    @Override
    public void replaceVariables(ArrayList<VariableNode> variables) {
    }

    @Override
    public void traverse() {
        ForNodeHandler.traverse(this);
    }

}
