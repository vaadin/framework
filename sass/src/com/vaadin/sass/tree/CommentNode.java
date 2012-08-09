/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.sass.tree;

public class CommentNode extends Node {
    private String comment;

    public CommentNode(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return comment;
    }
}
