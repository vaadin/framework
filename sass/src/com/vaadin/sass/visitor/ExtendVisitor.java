package com.vaadin.sass.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.css.sac.SelectorList;

import com.vaadin.sass.parser.SelectorListImpl;
import com.vaadin.sass.selector.SelectorUtil;
import com.vaadin.sass.tree.BlockNode;
import com.vaadin.sass.tree.ExtendNode;
import com.vaadin.sass.tree.Node;

public class ExtendVisitor implements Visitor {
    private Map<String, List<SelectorList>> extendsMap = new HashMap<String, List<SelectorList>>();

    @Override
    public void traverse(Node node) throws Exception {
        buildExtendsMap(node);
        modifyTree(node);
    }

    private void modifyTree(Node node) throws Exception {
        for (Node child : node.getChildren()) {
            if (child instanceof BlockNode) {
                BlockNode blockNode = (BlockNode) child;
                String selectorString = SelectorUtil.toString(blockNode
                        .getSelectorList());
                if (extendsMap.get(selectorString) != null) {
                    for (SelectorList sList : extendsMap.get(selectorString)) {
                        SelectorList newList = SelectorUtil
                                .createNewSelectorListFromAnOldOneWithSomPartReplaced(
                                        blockNode.getSelectorList(),
                                        selectorString, sList);
                        addAdditionalSelectorListToBlockNode(blockNode, newList);
                    }
                } else {
                    for (Entry<String, List<SelectorList>> entry : extendsMap
                            .entrySet()) {
                        if (selectorString.contains(entry.getKey())) {
                            for (SelectorList sList : entry.getValue()) {
                                SelectorList newList = SelectorUtil
                                        .createNewSelectorListFromAnOldOneWithSomPartReplaced(
                                                blockNode.getSelectorList(),
                                                entry.getKey(), sList);
                                addAdditionalSelectorListToBlockNode(blockNode,
                                        newList);
                            }
                        }
                    }
                }
            } else {
                buildExtendsMap(child);
            }
        }

    }

    private void buildExtendsMap(Node node) {
        if (node instanceof BlockNode) {
            BlockNode blockNode = (BlockNode) node;
            for (Node child : new ArrayList<Node>(node.getChildren())) {
                if (child instanceof ExtendNode) {
                    ExtendNode extendNode = (ExtendNode) child;
                    String extendedString = SelectorUtil.toString(extendNode
                            .getList());
                    if (extendsMap.get(extendedString) == null) {
                        extendsMap.put(extendedString,
                                new ArrayList<SelectorList>());
                    }
                    extendsMap.get(extendedString).add(
                            blockNode.getSelectorList());
                    node.removeChild(child);
                } else {
                    buildExtendsMap(child);
                }
            }
        } else {
            for (Node child : node.getChildren()) {
                buildExtendsMap(child);
            }
        }

    }

    private void addAdditionalSelectorListToBlockNode(BlockNode blockNode,
            SelectorList list) {
        if (list != null) {
            for (int i = 0; i < list.getLength(); i++) {
                ((SelectorListImpl) blockNode.getSelectorList())
                        .addSelector(list.item(i));
            }
        }
    }
}
