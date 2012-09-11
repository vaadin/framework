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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public abstract class Node implements Serializable {
    private static final long serialVersionUID = 5914711715839294816L;

    protected ArrayList<Node> children;
    private String fileName;

    protected String rawString;

    public Node() {
        children = new ArrayList<Node>();
    }

    public Node(String raw) {
        this();
        rawString = raw;
    }

    public void appendAll(Collection<Node> nodes) {
        if (nodes != null && !nodes.isEmpty()) {
            children.addAll(nodes);
        }
    }

    public void appendChild(Node node) {
        if (node != null) {
            children.add(node);
        }
    }

    public void appendChild(Node node, Node after) {
        if (node != null) {
            int index = children.indexOf(after);
            if (index != -1) {
                children.add(index + 1, node);
            } else {
                throw new NullPointerException("after-node was not found");
            }
        }
    }

    public void removeChild(Node node) {
        if (node != null) {
            children.remove(node);
        }
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Node> children) {
        this.children = children;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return "";
    }

    public String getRawString() {
        return rawString;
    }

}
