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

public class ForNode extends Node {
    private static final long serialVersionUID = -1159180539216623335L;

    String var;
    String from;
    String to;
    boolean exclusive;
    String body;

    public ForNode(String var, String from, String to, boolean exclusive,
            String body) {
        super();
        this.var = var;
        this.from = from;
        this.to = to;
        this.exclusive = exclusive;
        this.body = body;
    }

    @Override
    public String toString() {
        return "For Node: " + "{variable: " + var + ", from:" + from + ", to: "
                + to + ", exclusive: " + exclusive + ", body" + body;
    }

    @Override
    public void traverse() {

    }

}
