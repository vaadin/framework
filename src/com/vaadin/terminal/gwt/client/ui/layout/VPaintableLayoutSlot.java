/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.layout;

import com.vaadin.terminal.gwt.client.LayoutManager;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.VPaintableWidget;
import com.vaadin.terminal.gwt.client.ui.ManagedLayout;

public class VPaintableLayoutSlot extends VLayoutSlot {

    final VPaintableWidget paintable;
    private LayoutManager layoutManager;

    public VPaintableLayoutSlot(String baseClassName, VPaintableWidget paintable) {
        super(baseClassName, paintable.getWidget());
        this.paintable = paintable;
        layoutManager = paintable.getLayoutManager();
    }

    public VPaintableWidget getPaintable() {
        return paintable;
    }

    @Override
    protected int getCaptionHeight() {
        VCaption caption = getCaption();
        return caption != null ? layoutManager.getOuterHeight(caption
                .getElement()) : 0;
    }

    @Override
    protected int getCaptionWidth() {
        VCaption caption = getCaption();
        return caption != null ? layoutManager.getOuterWidth(caption
                .getElement()) : 0;
    }

    @Override
    public void setCaption(VCaption caption) {
        VCaption oldCaption = getCaption();
        if (oldCaption != null) {
            layoutManager.unregisterDependency(
                    (ManagedLayout) paintable.getParent(),
                    oldCaption.getElement());
        }
        super.setCaption(caption);
        if (caption != null) {
            layoutManager
                    .registerDependency((ManagedLayout) paintable.getParent(),
                            caption.getElement());
        }
    }

    @Override
    public int getWidgetHeight() {
        return layoutManager.getOuterHeight(paintable.getWidget()
                .getElement());
    }

    @Override
    public int getWidgetWidth() {
        return layoutManager.getOuterWidth(paintable.getWidget()
                .getElement());
    }

    @Override
    public boolean isUndefinedHeight() {
        return paintable.isUndefinedHeight();
    }

    @Override
    public boolean isUndefinedWidth() {
        return paintable.isUndefinedWidth();
    }

    @Override
    public boolean isRelativeHeight() {
        return paintable.isRelativeHeight();
    }

    @Override
    public boolean isRelativeWidth() {
        return paintable.isRelativeWidth();
    }
}
