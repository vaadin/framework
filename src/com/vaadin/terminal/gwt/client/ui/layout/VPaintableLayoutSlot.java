package com.vaadin.terminal.gwt.client.ui.layout;

import com.vaadin.terminal.gwt.client.MeasuredSize;
import com.vaadin.terminal.gwt.client.VCaption;
import com.vaadin.terminal.gwt.client.VPaintableWidget;

public class VPaintableLayoutSlot extends VLayoutSlot {

    final VPaintableWidget paintable;

    public VPaintableLayoutSlot(VPaintableWidget paintable) {
        super(paintable.getWidgetForPaintable());
        this.paintable = paintable;
    }

    public VPaintableWidget getPaintable() {
        return paintable;
    }

    @Override
    protected int getCaptionHeight() {
        VCaption caption = getCaption();
        return caption != null ? getParentSize().getDependencyOuterHeight(
                caption.getElement()) : 0;
    }

    private MeasuredSize getParentSize() {
        return paintable.getParent().getMeasuredSize();
    }

    @Override
    protected int getCaptionWidth() {
        VCaption caption = getCaption();
        return caption != null ? getParentSize().getDependencyOuterWidth(
                caption.getElement()) : 0;
    }

    @Override
    public void setCaption(VCaption caption) {
        VCaption oldCaption = getCaption();
        if (oldCaption != null) {
            getParentSize().deRegisterDependency(oldCaption.getElement());
        }
        super.setCaption(caption);
        if (caption != null) {
            getParentSize().registerDependency(caption.getElement());
        }
    }

    @Override
    public int getWidgetHeight() {
        return paintable.getMeasuredSize().getOuterHeight();
    }

    @Override
    public int getWidgetWidth() {
        return paintable.getMeasuredSize().getOuterWidth();
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
