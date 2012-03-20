/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.layout;

import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.LayoutManager;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.ui.ManagedLayout;

public class ComponentConnectorLayoutSlot extends VLayoutSlot {

    final ComponentConnector child;
    final ManagedLayout layout;

    public ComponentConnectorLayoutSlot(String baseClassName,
            ComponentConnector child, ManagedLayout layout) {
        super(baseClassName, child.getWidget());
        this.child = child;
        this.layout = layout;
    }

    public ComponentConnector getChild() {
        return child;
    }

    @Override
    protected int getCaptionHeight() {
        VCaption caption = getCaption();
        return caption != null ? getLayoutManager().getOuterHeight(
                caption.getElement()) : 0;
    }

    @Override
    protected int getCaptionWidth() {
        VCaption caption = getCaption();
        return caption != null ? getLayoutManager().getOuterWidth(
                caption.getElement()) : 0;
    }

    public LayoutManager getLayoutManager() {
        return layout.getLayoutManager();
    }

    @Override
    public void setCaption(VCaption caption) {
        VCaption oldCaption = getCaption();
        if (oldCaption != null) {
            getLayoutManager().unregisterDependency(layout,
                    oldCaption.getElement());
        }
        super.setCaption(caption);
        if (caption != null) {
            getLayoutManager().registerDependency(
                    (ManagedLayout) child.getParent(), caption.getElement());
        }
    }

    @Override
    public int getWidgetHeight() {
        return getLayoutManager()
                .getOuterHeight(child.getWidget().getElement());
    }

    @Override
    public int getWidgetWidth() {
        return getLayoutManager().getOuterWidth(child.getWidget().getElement());
    }

    @Override
    public boolean isUndefinedHeight() {
        return child.isUndefinedHeight();
    }

    @Override
    public boolean isUndefinedWidth() {
        return child.isUndefinedWidth();
    }

    @Override
    public boolean isRelativeHeight() {
        return child.isRelativeHeight();
    }

    @Override
    public boolean isRelativeWidth() {
        return child.isRelativeWidth();
    }
}
