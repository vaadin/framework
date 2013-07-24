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

import com.vaadin.sass.internal.visitor.ReturnNodeHandler;

/**
 * @version $Revision: 1.0 $
 * @author James Lefeu @ Liferay, Inc.
 */
public class ReturnNode extends Node implements IVariableNode {
    private static final long serialVersionUID = 3301805078983796870L;

    private static String expression;

    public ReturnNode() {
        super();
    }

    public void setExpression(String s) {
        expression = s;
    }

    public String getExpression() {
        return expression;
    }



    @Override
    public void replaceVariables(ArrayList<VariableNode> variables) {

    }

    @Override
    public void traverse() {
        try {
            ReturnNodeHandler.traverse(this);
            //getParentNode().removeChild(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
