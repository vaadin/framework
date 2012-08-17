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

package com.vaadin.sass.visitor;

import java.util.HashSet;
import java.util.Set;

import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;

import com.vaadin.sass.parser.SelectorListImpl;
import com.vaadin.sass.selector.CompositeSelector;
import com.vaadin.sass.tree.BlockNode;
import com.vaadin.sass.tree.Node;

public class BlockVisitor implements Visitor {

    @Override
    public void traverse(Node node) {
        traverse(null, node);
    }

    private void traverse(Node parent, Node node) {
        Node after = node;
        Set<Node> toBeDeleted = new HashSet<Node>();
        for (int pos = 0; pos < node.getChildren().size(); pos++) {
            Node child = node.getChildren().get(pos);

            traverse(node, child);

            if (child instanceof BlockNode && node instanceof BlockNode
                    && parent != null) {
                combineParentSelectorListToChild(node, child);
                toBeDeleted.add(child);
                parent.appendChild(child, after);
                after = child;
            }
        }
        for (Node child : toBeDeleted) {
            node.removeChild(child);
        }
    }

    private void combineParentSelectorListToChild(Node parent, Node child) {
        if (parent instanceof BlockNode && child instanceof BlockNode) {
            SelectorListImpl newList = new SelectorListImpl();
            SelectorList parentSelectors = ((BlockNode) parent)
                    .getSelectorList();
            SelectorList childSelectors = ((BlockNode) child).getSelectorList();
            for (int i = 0; i < parentSelectors.getLength(); i++) {
                Selector parentSelector = parentSelectors.item(i);
                for (int j = 0; j < childSelectors.getLength(); j++) {
                    Selector childSelector = childSelectors.item(j);
                    newList.addSelector(new CompositeSelector(parentSelector,
                            childSelector));
                }

            }
            ((BlockNode) child).setSelectorList(newList);
        }
    }
}
