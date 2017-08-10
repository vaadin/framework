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
package com.vaadin.client.ui.absolutelayout;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.DirectionalManagedLayout;
import com.vaadin.client.Util;
import com.vaadin.client.VCaption;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.communication.StateChangeEvent.StateChangeHandler;
import com.vaadin.client.ui.AbstractComponentContainerConnector;
import com.vaadin.client.ui.LayoutClickEventHandler;
import com.vaadin.client.ui.VAbsoluteLayout;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.LayoutClickRpc;
import com.vaadin.shared.ui.absolutelayout.AbsoluteLayoutServerRpc;
import com.vaadin.shared.ui.absolutelayout.AbsoluteLayoutState;
import com.vaadin.ui.AbsoluteLayout;

/**
 * Connects the server side {@link AbsoluteLayout} with the client side
 * counterpart {@link VAbsoluteLayout}
 */
@Connect(AbsoluteLayout.class)
public class AbsoluteLayoutConnector extends AbstractComponentContainerConnector
        implements DirectionalManagedLayout {

    private LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
            this) {

        @Override
        protected ComponentConnector getChildComponent(
                com.google.gwt.user.client.Element element) {
            return getConnectorForElement(element);
        }

        @Override
        protected LayoutClickRpc getLayoutClickRPC() {
            return getRpcProxy(AbsoluteLayoutServerRpc.class);
        }
    };

    private StateChangeHandler childStateChangeHandler = new StateChangeHandler() {
        @Override
        public void onStateChanged(StateChangeEvent stateChangeEvent) {
            ComponentConnector child = (ComponentConnector) stateChangeEvent
                    .getConnector();
            List<String> childStyles = child.getState().styles;
            if (childStyles == null) {
                getWidget().setWidgetWrapperStyleNames(child.getWidget(),
                        (String[]) null);
            } else {
                getWidget().setWidgetWrapperStyleNames(child.getWidget(),
                        childStyles.toArray(new String[childStyles.size()]));
            }

            if (stateChangeEvent.hasPropertyChanged("height") || stateChangeEvent.hasPropertyChanged("width")) {
                setChildWidgetPosition(child);
            }
        }
    };

    /**
     * Returns the deepest nested child component which contains "element". The
     * child component is also returned if "element" is part of its caption.
     *
     * @param element
     *            An element that is a nested sub element of the root element in
     *            this layout
     * @return The Paintable which the element is a part of. Null if the element
     *         belongs to the layout and not to a child.
     * @deprecated As of 7.2, call or override
     *             {@link #getConnectorForElement(Element)} instead
     */
    @Deprecated
    protected ComponentConnector getConnectorForElement(
            com.google.gwt.user.client.Element element) {
        return Util.getConnectorForElement(getConnection(), getWidget(),
                element);
    }

    /**
     * Returns the deepest nested child component which contains "element". The
     * child component is also returned if "element" is part of its caption.
     *
     * @param element
     *            An element that is a nested sub element of the root element in
     *            this layout
     * @return The Paintable which the element is a part of. Null if the element
     *         belongs to the layout and not to a child.
     *
     * @since 7.2
     */
    protected ComponentConnector getConnectorForElement(Element element) {
        return getConnectorForElement(DOM.asOld(element));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.client.HasComponentsConnector#updateCaption(com.vaadin
     * .client.ComponentConnector)
     */
    @Override
    public void updateCaption(ComponentConnector component) {
        VAbsoluteLayout absoluteLayoutWidget = getWidget();
        boolean captionIsNeeded = VCaption.isNeeded(component.getState());

        VCaption caption = absoluteLayoutWidget
                .getWidgetCaption(component.getWidget());
        if (captionIsNeeded) {
            if (caption == null) {
                caption = new VCaption(component, getConnection());
            }
            absoluteLayoutWidget.setWidgetCaption(component.getWidget(),
                    caption);
        } else if (caption != null) {
            absoluteLayoutWidget.setWidgetCaption(component.getWidget(), null);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.client.ui.AbstractComponentConnector#getWidget()
     */
    @Override
    public VAbsoluteLayout getWidget() {
        return (VAbsoluteLayout) super.getWidget();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.client.ui.AbstractComponentConnector#getState()
     */
    @Override
    public AbsoluteLayoutState getState() {
        return (AbsoluteLayoutState) super.getState();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vaadin.client.ui.AbstractComponentConnector#onStateChanged(com.vaadin
     * .client.communication.StateChangeEvent)
     */
    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        clickEventHandler.handleEventHandlerRegistration();

        // TODO Margin handling

        for (ComponentConnector child : getChildComponents()) {
            setChildWidgetPosition(child);
        }
    }

    private void setChildWidgetPosition(ComponentConnector child) {
        String position = getState().connectorToCssPosition
                .get(child.getConnectorId());
        if (position == null) {
            position = "";
        }
        // make sure relative sizes get displayed correctly
        String width = child.getState().width;
        if (width != null && width.endsWith("%")) {
            position = addDefaultPositionIfMissing(position, "left");
            position = addDefaultPositionIfMissing(position, "right");
        }
        String height = child.getState().height;
        if (height != null && height.endsWith("%")) {
            position = addDefaultPositionIfMissing(position, "top");
            position = addDefaultPositionIfMissing(position, "bottom");
        }
        getWidget().setWidgetPosition(child.getWidget(), position);
    }

    /**
     * Adds default value of 0.0px for the given property if it's missing from
     * the position string altogether. If the property value is already set no
     * changes are needed.
     *
     * @param position
     *            original position styles
     * @param property
     *            the property that needs to have a value
     * @return updated position, or the original string if no updates were
     *         needed
     */
    private String addDefaultPositionIfMissing(String position,
            String property) {
        if (!position.contains(property)) {
            position = position + property + ":0.0px;";
        }
        return position;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.client.ui.AbstractComponentContainerConnector#
     * onConnectorHierarchyChange
     * (com.vaadin.client.ConnectorHierarchyChangeEvent)
     */
    @Override
    public void onConnectorHierarchyChange(
            ConnectorHierarchyChangeEvent event) {
        for (ComponentConnector child : getChildComponents()) {
            if (!getWidget().contains(child.getWidget())) {
                getWidget().add(child.getWidget());
                child.addStateChangeHandler(childStateChangeHandler);
                setChildWidgetPosition(child);
            }
        }
        for (ComponentConnector oldChild : event.getOldChildren()) {
            if (oldChild.getParent() != this) {
                getWidget().remove(oldChild.getWidget());
                oldChild.removeStateChangeHandler(childStateChangeHandler);
            }
        }

        getWidget().cleanupWrappers();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.client.DirectionalManagedLayout#layoutVertically()
     */
    @Override
    public void layoutVertically() {
        getWidget().layoutVertically();
        for (ComponentConnector connector : getChildComponents()) {
            if (connector.isRelativeHeight()) {
                getLayoutManager().reportHeightAssignedToRelative(connector,
                        getWidget().getWidgetSlotHeight(connector.getWidget()));
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.client.DirectionalManagedLayout#layoutHorizontally()
     */
    @Override
    public void layoutHorizontally() {
        getWidget().layoutHorizontally();
        for (ComponentConnector connector : getChildComponents()) {
            if (connector.isRelativeWidth()) {
                getLayoutManager().reportWidthAssignedToRelative(connector,
                        getWidget().getWidgetSlotWidth(connector.getWidget()));
            }
        }
    }
}
