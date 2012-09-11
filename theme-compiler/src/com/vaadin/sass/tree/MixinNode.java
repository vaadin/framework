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
import java.util.Collection;

import org.w3c.css.sac.LexicalUnit;

import com.vaadin.sass.parser.LexicalUnitImpl;

public class MixinNode extends Node implements IVariableNode {
    private static final long serialVersionUID = 4725008226813110658L;

    private String name;
    private ArrayList<LexicalUnit> arglist;

    public MixinNode(String name, Collection<LexicalUnit> args) {
        super();
        this.name = name;
        arglist = new ArrayList<LexicalUnit>();
        if (args != null && !args.isEmpty()) {
            arglist.addAll(args);
        }
    }

    @Override
    public String toString() {
        return "name: " + name + " args: " + arglist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<LexicalUnit> getArglist() {
        return arglist;
    }

    public void setArglist(ArrayList<LexicalUnit> arglist) {
        this.arglist = arglist;
    }

    @Override
    public void replaceVariables(ArrayList<VariableNode> variables) {
        for (final VariableNode var : variables) {
            for (final LexicalUnit arg : new ArrayList<LexicalUnit>(arglist)) {
                if (arg.getLexicalUnitType() == LexicalUnitImpl.SCSS_VARIABLE
                        && arg.getStringValue().equals(var.getName())) {
                    ((LexicalUnitImpl) arg).replaceValue(var.getExpr());
                }
            }
        }
    }

}
