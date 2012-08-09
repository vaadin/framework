/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.sass.visitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.css.sac.LexicalUnit;

import com.vaadin.sass.parser.LexicalUnitImpl;
import com.vaadin.sass.parser.SCSSLexicalUnit;
import com.vaadin.sass.tree.Node;
import com.vaadin.sass.tree.RuleNode;
import com.vaadin.sass.tree.VariableNode;

public class VariableVisitor implements Visitor {

    @Override
    public void traverse(Node node) {
        Map<String, LexicalUnitImpl> variables = new HashMap<String, LexicalUnitImpl>();
        traverse(node, variables);
    }

    private void traverse(Node node, Map<String, LexicalUnitImpl> variables) {
        if (node instanceof RuleNode) {
            LexicalUnit value = ((RuleNode) node).getValue();
            updateValue(value, variables);
        } else {
            Set<Node> toBeDeleted = new HashSet<Node>();
            for (Node child : node.getChildren()) {
                if (child instanceof VariableNode) {
                    variables.put(((VariableNode) child).getName(),
                            (LexicalUnitImpl) ((VariableNode) child).getExpr());
                    toBeDeleted.add(child);
                } else {
                    traverse(child, new HashMap<String, LexicalUnitImpl>(
                            variables));
                }
            }
            for (Node child : toBeDeleted) {
                node.removeChild(child);
            }
        }
    }

    private void updateValue(LexicalUnit value,
            Map<String, LexicalUnitImpl> variables) {
        if (value == null) {
            return;
        }
        if (value.getLexicalUnitType() == SCSSLexicalUnit.SCSS_VARIABLE) {
            LexicalUnitImpl variableValue = variables.get(
                    value.getStringValue()).clone();
            if (variableValue != null) {
                LexicalUnitImpl lexVal = (LexicalUnitImpl) value;
                lexVal.replaceValue(variableValue);
            }
        } else if (value.getLexicalUnitType() == SCSSLexicalUnit.SAC_FUNCTION) {
            LexicalUnit params = value.getParameters();
            updateValue(params, variables);
        }
        LexicalUnit next = value.getNextLexicalUnit();
        updateValue(next, variables);
    }
}
