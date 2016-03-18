/*
 * Copyright 2000-2014 Vaadin Ltd.
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
    protected void reportActualRelativeHeight(int allocatedHeight) {
        getLayoutManager().reportOuterHeight(child, allocatedHeight);
    }

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
