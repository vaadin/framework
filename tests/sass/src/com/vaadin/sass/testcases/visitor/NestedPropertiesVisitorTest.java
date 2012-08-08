package com.vaadin.sass.testcases.visitor;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.sass.ScssStylesheet;
import com.vaadin.sass.tree.NestPropertiesNode;
import com.vaadin.sass.tree.RuleNode;
import com.vaadin.sass.tree.VariableNode;
import com.vaadin.sass.visitor.NestPropertiesVisitor;

public class NestedPropertiesVisitorTest {
    private NestPropertiesVisitor visitor = new NestPropertiesVisitor();

    @Test
    public void testEmptyTreeNoChange() {
        ScssStylesheet root = new ScssStylesheet();
        Assert.assertFalse(root.hasChildren());
        visitor.traverse(root);
        Assert.assertFalse(root.hasChildren());
    }

    @Test
    public void testNoNestPropertiesNodeNoChange() {
        ScssStylesheet root = new ScssStylesheet();
        root.appendChild(new VariableNode("", ""));
        Assert.assertEquals(1, root.getChildren().size());
        visitor.traverse(root);
        Assert.assertEquals(1, root.getChildren().size());
    }

    @Test
    public void testNestedPropertiesCanBeUnnested() {
        ScssStylesheet root = new ScssStylesheet();
        NestPropertiesNode nested = new NestPropertiesNode("nested");
        RuleNode child0 = new RuleNode("child0", null, false, null);
        RuleNode child1 = new RuleNode("child1", null, true, null);
        nested.appendChild(child0);
        nested.appendChild(child1);
        root.appendChild(nested);

        Assert.assertEquals(1, root.getChildren().size());
        visitor.traverse(root);
        Assert.assertEquals(2, root.getChildren().size());

        for (int i = 0; i < root.getChildren().size(); i++) {
            RuleNode node = (RuleNode) root.getChildren().get(i);
            Assert.assertEquals("nested-child" + i, node.getVariable());
        }
    }
}
