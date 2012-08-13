/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.sass.visitor;

import java.util.ArrayList;

import org.w3c.css.sac.SelectorList;

import com.vaadin.sass.selector.SelectorUtil;
import com.vaadin.sass.tree.BlockNode;
import com.vaadin.sass.tree.Node;

public class ParentSelectorVisitor implements Visitor {

    @Override
    public void traverse(Node node) throws Exception {
        for (Node child : new ArrayList<Node>(node.getChildren())) {
            if (child instanceof BlockNode) {
                traverse(node, (BlockNode) child);
            }
        }
    }

    private void traverse(Node parent, BlockNode block) throws Exception {
        Node pre = block;
        for (Node child : new ArrayList<Node>(block.getChildren())) {
            if (child instanceof BlockNode) {
                BlockNode blockChild = (BlockNode) child;
                traverse(block, blockChild);
                if (SelectorUtil
                        .hasParentSelector(blockChild.getSelectorList())) {
                    parent.appendChild(child, pre);
                    pre = child;
                    block.removeChild(child);
                    SelectorList newSelectorList = SelectorUtil
                            .createNewSelectorListFromAnOldOneWithSomPartReplaced(
                                    blockChild.getSelectorList(), "&",
                                    block.getSelectorList());
                    blockChild.setSelectorList(newSelectorList);
                }
            }
        }
    }
}
