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
import java.util.Collection;
import java.util.Map;

import com.vaadin.sass.internal.tree.MixinNode;
import com.vaadin.sass.internal.visitor.FunctionNodeHandler;


/**
 * @version $Revision: 1.0 $
 * @author James Lefeu @ Liferay, Inc.
 */
public class FunctionNode extends MixinNode implements IVariableNode {
    private static final long serialVersionUID = 4725008226813110659L;
    private boolean isFunctionNode;

    public FunctionNode(String name) {
        super();
        isFunctionNode = true;
    }

    public FunctionNode(String name, Collection<LexicalUnitImpl> args) {
        super();
        isFunctionNode = true;
    }

    @Override
    public void traverse() {
        try {
            // limit variable scope to the function
            Map<String, VariableNode> variableScope = ScssStylesheet
                    .openVariableScope();

            replaceVariables(ScssStylesheet.getVariables());
            replaceVariablesForChildren();
            FunctionNodeHandler.traverse(this);

            ScssStylesheet.closeVariableScope(variableScope);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
