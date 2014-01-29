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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.sass.internal.tree.Node;
import com.vaadin.sass.internal.visitor.IfElseNodeHandler;

public class IfElseDefNode extends Node {

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (final Node child : getChildren()) {
            b.append(child.toString());
            b.append("\n");
        }
        return b.toString();
    }

    @Override
    public void traverse() {
        try {

            for (final Node child : children) {
                child.traverse();
            }

            IfElseNodeHandler.traverse(this);
        } catch (Exception e) {
            Logger.getLogger(IfElseDefNode.class.getName()).log(Level.SEVERE,
                    null, e);
        }
    }

}
