/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.twincolselect;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.DirectionalManagedLayout;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.terminal.gwt.client.ui.optiongroup.OptionGroupBaseConnector;
import com.vaadin.ui.TwinColSelect;

@Connect(TwinColSelect.class)
public class TwinColSelectConnector extends OptionGroupBaseConnector implements
        DirectionalManagedLayout {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Captions are updated before super call to ensure the widths are set
        // correctly
        if (isRealUpdate(uidl)) {
            getWidget().updateCaptions(uidl);
            getLayoutManager().setNeedsHorizontalLayout(this);
        }

        super.updateFromUIDL(uidl, client);
    }

    @Override
    protected void init() {
        getLayoutManager().registerDependency(this,
                getWidget().captionWrapper.getElement());
    }

    @Override
    public void onUnregister() {
        getLayoutManager().unregisterDependency(this,
                getWidget().captionWrapper.getElement());
    }

    @Override
    public VTwinColSelect getWidget() {
        return (VTwinColSelect) super.getWidget();
    }

    public void layoutVertically() {
        if (isUndefinedHeight()) {
            getWidget().clearInternalHeights();
        } else {
            getWidget().setInternalHeights();
        }
    }

    public void layoutHorizontally() {
        if (isUndefinedWidth()) {
            getWidget().clearInternalWidths();
        } else {
            getWidget().setInternalWidths();
        }
    }
}
