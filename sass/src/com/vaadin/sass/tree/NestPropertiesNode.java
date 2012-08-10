/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.sass.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NestPropertiesNode extends Node {
    private static final long serialVersionUID = 3671253315690598308L;

    public NestPropertiesNode(String name) {
        super();
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<RuleNode> unNesting() {
        List<RuleNode> result = new ArrayList<RuleNode>();
        for (Node child : children) {
            result.add(createNewRuleNodeFromChild((RuleNode) child));
        }
        return result;
    }

    public RuleNode createNewRuleNodeFromChild(RuleNode child) {
        StringBuilder builder = new StringBuilder(name);
        builder.append("-").append(child.getVariable());
        RuleNode newRuleNode = new RuleNode(builder.toString(),
                child.getValue(), child.isImportant(), null);
        return newRuleNode;
    }
}
