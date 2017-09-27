/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.client.ui.formlayout;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.Util;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractLayoutConnector;
import com.vaadin.client.ui.HasErrorIndicator;
import com.vaadin.client.ui.LayoutClickEventHandler;
import com.vaadin.client.ui.PostLayoutListener;
import com.vaadin.client.ui.VFormLayout;
import com.vaadin.client.ui.VFormLayout.Caption;
import com.vaadin.client.ui.VFormLayout.ErrorFlag;
import com.vaadin.client.ui.VFormLayout.VFormLayoutTable;
import com.vaadin.client.ui.layout.ElementResizeEvent;
import com.vaadin.client.ui.layout.ElementResizeListener;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.LayoutClickRpc;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.orderedlayout.AbstractOrderedLayoutServerRpc;
import com.vaadin.shared.ui.orderedlayout.FormLayoutState;
import com.vaadin.ui.FormLayout;

@Connect(FormLayout.class)
public class FormLayoutConnector extends AbstractLayoutConnector
        implements PostLayoutListener {

    /*
     * Handlers & Listeners
     */

    private LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
            this) {

        @Override
        protected ComponentConnector getChildComponent(
                com.google.gwt.user.client.Element element) {
            return Util.getConnectorForElement(getConnection(), getWidget(),
                    element);
        }

        @Override
        protected LayoutClickRpc getLayoutClickRPC() {
            return getRpcProxy(AbstractOrderedLayoutServerRpc.class);
        }
    };

    private Map<ComponentConnector, String> oldMaxWidths = null;

    private static final ElementResizeListener dummyFirstCellResizeListener = new ElementResizeListener() {
        @Override
        public void onElementResize(ElementResizeEvent e) {
            // Ignore event, listener added just to make measurements available
        }
    };

    // Detects situations when there's something inside the FormLayout that
    // prevents it from shrinking
    private ElementResizeListener resizeListener = new ElementResizeListener() {
        @Override
        public void onElementResize(ElementResizeEvent e) {
            LayoutManager layoutManager = getLayoutManager();
            double tableWidth = layoutManager
                    .getOuterWidthDouble(getWidget().table.getElement());
            double ownWidth = layoutManager
                    .getInnerWidthDouble(getWidget().getElement());
            if (ownWidth < tableWidth) {
                // Something inside the table prevents it from shrinking,
                // temporarily force column widths
                double excessWidth = tableWidth - ownWidth;

                // All td elements in the component column have the same width,
                // so we only need to check the width of the first one to know
                // how wide the column is.
                Element firstComponentTd = findFirstComponentTd();
                if (firstComponentTd == null) {
                    // Can't do anything if there are no rows
                    return;
                }

                double componentColWidth = layoutManager
                        .getOuterWidthDouble(firstComponentTd);

                if (componentColWidth == -1) {
                    // Didn't get a proper width reading, best to not touch
                    // anything
                    return;
                }

                // Restrict content td width
                // Round down to prevent interactions with fractional sizes of
                // other columns
                int targetWidth = (int) Math
                        .floor(componentColWidth - excessWidth);

                // Target might be negative if captions are wider than the total
                // available width
                targetWidth = Math.max(0, targetWidth);

                if (oldMaxWidths == null) {
                    oldMaxWidths = new HashMap<>();
                }

                for (ComponentConnector child : getChildComponents()) {
                    Element childElement = child.getWidget().getElement();
                    if (!oldMaxWidths.containsKey(child)) {
                        oldMaxWidths.put(child,
                                childElement.getPropertyString("maxWidth"));
                    }
                    childElement.getStyle().setPropertyPx("maxWidth",
                            targetWidth);
                    layoutManager.reportOuterWidth(child, targetWidth);
                }
            }
        }
    };

    @Override
    protected void init() {
        super.init();
        getLayoutManager().addElementResizeListener(
                getWidget().table.getElement(), resizeListener);
        getLayoutManager().addElementResizeListener(getWidget().getElement(),
                resizeListener);
        addComponentCellListener();
    }

    @Override
    public void onUnregister() {
        getLayoutManager().removeElementResizeListener(
                getWidget().table.getElement(), resizeListener);
        getLayoutManager().removeElementResizeListener(getWidget().getElement(),
                resizeListener);
        removeComponentCellListener();
        super.onUnregister();
    }

    @Override
    public FormLayoutState getState() {
        return (FormLayoutState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        clickEventHandler.handleEventHandlerRegistration();
        VFormLayoutTable formLayoutTable = getWidget().table;

        formLayoutTable.setMargins(new MarginInfo(getState().marginsBitmask));
        formLayoutTable.setSpacing(getState().spacing);

    }

    @Override
    public void onConnectorHierarchyChange(
            ConnectorHierarchyChangeEvent event) {
        VFormLayout formLayout = getWidget();
        VFormLayoutTable formLayoutTable = getWidget().table;

        removeComponentCellListener();

        int childId = 0;

        formLayoutTable.setRowCount(getChildComponents().size());

        for (ComponentConnector child : getChildComponents()) {
            Widget childWidget = child.getWidget();

            Caption caption = formLayoutTable.getCaption(childWidget);
            if (caption == null) {
                caption = formLayout.new Caption(child);
                caption.addClickHandler(formLayoutTable);
            }

            ErrorFlag error = formLayoutTable.getError(childWidget);
            if (error == null) {
                error = formLayout.new ErrorFlag(child);
            }

            formLayoutTable.setChild(childId, childWidget, caption, error);
            childId++;
        }

        for (ComponentConnector oldChild : event.getOldChildren()) {
            if (oldChild.getParent() == this) {
                continue;
            }

            formLayoutTable.cleanReferences(oldChild.getWidget());
        }

        addComponentCellListener();
    }

    private void addComponentCellListener() {
        Element td = findFirstComponentTd();
        if (td != null) {
            getLayoutManager().addElementResizeListener(td,
                    dummyFirstCellResizeListener);
        }
    }

    private void removeComponentCellListener() {
        Element td = findFirstComponentTd();
        if (td != null) {
            getLayoutManager().removeElementResizeListener(td,
                    dummyFirstCellResizeListener);
        }
    }

    private Element findFirstComponentTd() {
        VFormLayoutTable table = getWidget().table;
        if (table.getRowCount() == 0) {
            return null;
        } else {
            return table.getCellFormatter().getElement(0,
                    VFormLayoutTable.COLUMN_WIDGET);
        }
    }

    @Override
    public void updateCaption(ComponentConnector component) {
        getWidget().table.updateCaption(component.getWidget(),
                component.getState(), component.isEnabled());
        boolean hideErrors = false;

        if (component instanceof HasErrorIndicator) {
            hideErrors = !((HasErrorIndicator) component)
                    .isErrorIndicatorVisible();
        }

        getWidget().table.updateError(component.getWidget(),
                component.getState().errorMessage,
                component.getState().errorLevel, hideErrors);
    }

    @Override
    public VFormLayout getWidget() {
        return (VFormLayout) super.getWidget();
    }

    @Override
    public TooltipInfo getTooltipInfo(Element element) {
        TooltipInfo info = null;

        if (element != getWidget().getElement()) {
            Object node = WidgetUtil.findWidget(element,
                    VFormLayout.Caption.class);

            if (node != null) {
                VFormLayout.Caption caption = (VFormLayout.Caption) node;
                info = caption.getOwner().getTooltipInfo(element);
            } else {

                node = WidgetUtil.findWidget(element,
                        VFormLayout.ErrorFlag.class);

                if (node != null) {
                    VFormLayout.ErrorFlag flag = (VFormLayout.ErrorFlag) node;
                    info = flag.getOwner().getTooltipInfo(element);
                }
            }
        }

        if (info == null) {
            info = super.getTooltipInfo(element);
        }

        return info;
    }

    @Override
    public boolean hasTooltip() {
        /*
         * Tooltips are fetched from child connectors -> there's no quick way of
         * checking whether there might a tooltip hiding somewhere
         */
        return true;
    }

    @Override
    public void postLayout() {
        if (oldMaxWidths != null) {
            for (ComponentConnector child : getChildComponents()) {
                Element childNode = child.getWidget().getElement();
                String oldValue = oldMaxWidths.get(child);
                if (oldValue == null) {
                    childNode.getStyle().clearProperty("maxWidth");
                } else {
                    childNode.getStyle().setProperty("maxWidth", oldValue);
                }
            }
            oldMaxWidths = null;
        }
    }

}
