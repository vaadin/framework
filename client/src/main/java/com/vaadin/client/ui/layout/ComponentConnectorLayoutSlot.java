/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.client.ui.layout;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.VCaption;
import com.vaadin.client.ui.ManagedLayout;

/**
 * A slot class implementation for ManagedLayout cells.
 *
 * @author Vaadin Ltd
 */
public class ComponentConnectorLayoutSlot extends VLayoutSlot {

    final ComponentConnector child;
    final ManagedLayout layout;

    /**
     * Constructs a slot instance for a ManagedLayout cell.
     *
     * @param baseClassName
     *            the base class name of the layout
     * @param child
     *            the connector of the child component whose widget should be
     *            set to this slot, should not be {@code null}
     * @param layout
     *            the managed layout that contains this slot
     */
    public ComponentConnectorLayoutSlot(String baseClassName,
            ComponentConnector child, ManagedLayout layout) {
        super(baseClassName, child.getWidget());
        this.child = child;
        this.layout = layout;
    }

    /**
     * Returns the connector of the child component that has been assigned to
     * this slot.
     *
     * @return the content connector
     */
    public ComponentConnector getChild() {
        return child;
    }

    @Override
    protected int getCaptionHeight() {
        VCaption caption = getCaption();
        return caption != null
                ? getLayoutManager().getOuterHeight(caption.getElement())
                : 0;
    }

    @Override
    protected int getCaptionWidth() {
        VCaption caption = getCaption();
        return caption != null
                ? getLayoutManager().getOuterWidth(caption.getElement())
                : 0;
    }

    /**
     * Returns the layout manager for the managed layout.
     *
     * @return layout manager
     */
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

    /**
     * Reports the expected outer height to the LayoutManager.
     *
     * @param allocatedHeight
     *            the height to set (including margins, borders and paddings) in
     *            pixels
     */
    @Override
    protected void reportActualRelativeHeight(int allocatedHeight) {
        getLayoutManager().reportOuterHeight(child, allocatedHeight);
    }

    /**
     * Reports the expected outer width to the LayoutManager.
     *
     * @param allocatedWidth
     *            the width to set (including margins, borders and paddings) in
     *            pixels
     */
    @Override
    protected void reportActualRelativeWidth(int allocatedWidth) {
        getLayoutManager().reportOuterWidth(child, allocatedWidth);
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
