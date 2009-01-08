package com.itmill.toolkit.ui;

/**
 * Vertical layout
 * 
 * <code>VerticalLayout</code> is a component container, which shows the
 * subcomponents in the order of their addition (vertically). A vertical layout
 * is by default 100% wide.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.3
 */
public class VerticalLayout extends AbstractOrderedLayout {

    public VerticalLayout() {
        setWidth("100%");
    }

    @Override
    public String getTag() {
        return "verticallayout";
    }

}
