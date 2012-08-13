/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.sass.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Node implements Serializable {
    private static final long serialVersionUID = 5914711715839294816L;

    protected ArrayList<Node> children;
    private String fileName;

    protected String rawString;
    protected List<String> variables;

    public Node() {
        children = new ArrayList<Node>();
        variables = new ArrayList<String>();
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

    public void addVariable(String var) {
        variables.add(var);
    }

    public void removeVariable(String var) {
        variables.remove(var);
    }
}
