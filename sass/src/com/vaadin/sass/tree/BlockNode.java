package com.vaadin.sass.tree;

import org.w3c.css.sac.SelectorList;

import com.vaadin.sass.parser.SelectorListImpl;
import com.vaadin.sass.selector.SelectorUtil;

public class BlockNode extends Node {

    private static final long serialVersionUID = 5742962631468325048L;

    SelectorList selectorList;

    public BlockNode(SelectorList selectorList) {
        this.selectorList = selectorList;
    }

    public SelectorList getSelectorList() {
        return selectorList;
    }

    public void setSelectorList(SelectorList selectorList) {
        this.selectorList = selectorList;
    }

    public String toString(boolean indent) {
        StringBuilder string = new StringBuilder();
        string.append(SelectorUtil.toString(selectorList));
        string.append(" {\n");
        for (Node child : children) {
            if (indent) {
                string.append("\t");
            }
            string.append("\t" + child.toString() + "\n");
        }
        if (indent) {
            string.append("\t");
        }
        string.append("}");
        return string.toString();
    }

    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        SelectorListImpl clonedSelectorList = new SelectorListImpl();
        for (int i = 0; i < selectorList.getLength(); i++) {
            clonedSelectorList.addSelector(selectorList.item(i));
        }
        return null;
        // BlockNode clone = new BlockNode()
    }
}
