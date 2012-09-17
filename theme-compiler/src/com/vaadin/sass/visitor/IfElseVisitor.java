package com.vaadin.sass.visitor;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.JexlException;
import org.w3c.flute.parser.ParseException;

import com.vaadin.sass.tree.Node;
import com.vaadin.sass.tree.controldirective.ElseNode;
import com.vaadin.sass.tree.controldirective.IfElseDefNode;
import com.vaadin.sass.tree.controldirective.IfElseNode;
import com.vaadin.sass.tree.controldirective.IfNode;

public class IfElseVisitor implements Visitor {

    private HashMap<Node, IfElseDefNode> controlDefs = new HashMap<Node, IfElseDefNode>();

    private static final JexlEngine evaluator = new JexlEngine();
    private static final Pattern pattern = Pattern
            .compile("[a-zA-Z0-9]*[a-zA-Z]+[a-zA-Z0-9]*");

    @Override
    public void traverse(Node node) throws Exception {
        addControlDefs(node, node);

        for (final Entry<Node, IfElseDefNode> entry : controlDefs.entrySet()) {
            IfElseDefNode defNode = entry.getValue();
            Node parent = entry.getKey();
            for (final Node child : defNode.getChildren()) {
                if (child instanceof IfNode) {
                    try {
                        String expression = ((IfElseNode) child)
                                .getExpression();
                        // We need to add ' ' for strings in the expression for
                        // jexl to understand that is should do a string
                        // comparison
                        expression = replaceStrings(expression);
                        Expression e = evaluator.createExpression(expression);
                        try {
                            Boolean result = (Boolean) e.evaluate(null);
                            if (result) {
                                replaceDefNodeWithCorrectChild(defNode, parent,
                                        child);
                                break;
                            }
                        } catch (ClassCastException ex) {
                            throw new ParseException(
                                    "Invalid @if/@else in scss file, not a boolean expression : "
                                            + child.toString());
                        } catch (NullPointerException ex) {
                            throw new ParseException(
                                    "Invalid @if/@else in scss file, not a boolean expression : "
                                            + child.toString());
                        }
                    } catch (JexlException e) {
                        throw new ParseException(
                                "Invalid @if/@else in scss file for "
                                        + child.toString());
                    }
                } else {
                    if (!(child instanceof ElseNode)
                            && defNode.getChildren().indexOf(child) == defNode
                                    .getChildren().size() - 1) {
                        throw new ParseException(
                                "Invalid @if/@else in scss file for " + defNode);
                    } else {
                        replaceDefNodeWithCorrectChild(defNode, parent, child);
                        break;
                    }
                }
            }

            parent.removeChild(defNode);
        }

    }

    private String replaceStrings(String expression) {
        Matcher m = pattern.matcher(expression);
        StringBuffer b = new StringBuffer();
        while (m.find()) {
            String group = m.group();
            m.appendReplacement(b, "'" + group + "'");
        }

        if (b.length() != 0) {
            return b.toString();
        }
        return expression;
    }

    private void replaceDefNodeWithCorrectChild(IfElseDefNode defNode,
            Node parent, final Node child) {
        for (final Node n : child.getChildren()) {
            parent.appendChild(n, defNode);
        }
    }

    private void addControlDefs(Node current, Node node) {
        for (Node child : current.getChildren()) {
            addControlDefs(node, child);
            if (child instanceof IfElseDefNode) {
                controlDefs.put(current, (IfElseDefNode) child);
            }
        }
    }
}
