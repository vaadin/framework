/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.formlayout;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangeEvent;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.AbstractFieldConnector;
import com.vaadin.terminal.gwt.client.ui.AbstractLayoutConnector;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.terminal.gwt.client.ui.VMarginInfo;
import com.vaadin.terminal.gwt.client.ui.formlayout.VFormLayout.Caption;
import com.vaadin.terminal.gwt.client.ui.formlayout.VFormLayout.ErrorFlag;
import com.vaadin.terminal.gwt.client.ui.formlayout.VFormLayout.VFormLayoutTable;
import com.vaadin.terminal.gwt.client.ui.orderedlayout.AbstractOrderedLayoutState;
import com.vaadin.ui.FormLayout;

@Connect(FormLayout.class)
public class FormLayoutConnector extends AbstractLayoutConnector {

    @Override
    public AbstractOrderedLayoutState getState() {
        return (AbstractOrderedLayoutState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        VFormLayoutTable formLayoutTable = getWidget().table;

        formLayoutTable.setMargins(new VMarginInfo(getState()
                .getMarginsBitmask()));
        formLayoutTable.setSpacing(getState().isSpacing());

    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        super.onConnectorHierarchyChange(event);

        VFormLayout formLayout = getWidget();
        VFormLayoutTable formLayoutTable = getWidget().table;

        int childId = 0;

        formLayoutTable.setRowCount(getChildren().size());

        for (ComponentConnector child : getChildren()) {
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

    }

    public void updateCaption(ComponentConnector component) {
        getWidget().table.updateCaption(component.getWidget(),
                component.getState(), component.isEnabled());
        boolean hideErrors = false;

        // FIXME This incorrectly depends on AbstractFieldConnector
        if (component instanceof AbstractFieldConnector) {
            hideErrors = ((AbstractFieldConnector) component).getState()
                    .isHideErrors();
        }

        getWidget().table.updateError(component.getWidget(), component
                .getState().getErrorMessage(), hideErrors);
    }

    @Override
    public VFormLayout getWidget() {
        return (VFormLayout) super.getWidget();
    }

}
