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

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.util.DeepCopy;

import com.vaadin.sass.internal.tree.MixinDefNode;

/**
 * @version $Revision: 1.0 $
 * @author James Lefeu @ Liferay, Inc.
 */
public class FunctionDefNode extends MixinDefNode implements IVariableNode {
    private static final long serialVersionUID = 5469294053247343949L;
    private boolean isFunctionDefNode;


    public FunctionDefNode(String name, Collection<VariableNode> args) {
        super();
        isFunctionDefNode = true;
    }

    @Override
    public String toString() {
        return "Function Definition Node: {name: " + name + ", args: "
                + arglist.size() + ", body: " + body + "}";
    }

    /**
     * This should only happen on a cloned FunctionDefNode, since it changes the
     * Node itself.
     * 
     * @param functionNode
     * @return
     */
    public FunctionDefNode replaceContentDirective(FunctionNode functionNode) {
        return (FunctionDefNode)findAndReplaceContentNodeInChildren(
            this, (MixinNode)functionNode);
    }

     public FunctionDefNode replaceContentNode(ContentNode contentNode,
            FunctionNode functionNode) {

        return (FunctionDefNode)replaceContentNode(contentNode, (MixinNode)functionNode);
    }
}
