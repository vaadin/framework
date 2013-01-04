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

public class WhileNode extends Node {
    private static final long serialVersionUID = 7593896018196027279L;

    private String condition;
    private String body;

    public WhileNode(String condition, String body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String toString() {
        return "While Node: { condition: " + condition + ", body:" + body + "}";
    }

    @Override
    public void traverse() {

    }

}
