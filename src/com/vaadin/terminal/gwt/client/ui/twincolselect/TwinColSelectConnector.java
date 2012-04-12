/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.twincolselect;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.DirectionalManagedLayout;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.Component;
import com.vaadin.terminal.gwt.client.ui.optiongroup.OptionGroupBaseConnector;
import com.vaadin.ui.TwinColSelect;

@Component(TwinColSelect.class)
public class TwinColSelectConnector extends OptionGroupBaseConnector implements
        DirectionalManagedLayout {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Captions are updated before super call to ensure the widths are set
        // correctly
        if (isRealUpdate(uidl)) {
            getWidget().updateCaptions(uidl);
            getLayoutManager().setWidthNeedsUpdate(this);
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
    protected Widget createWidget() {
        return GWT.create(VTwinColSelect.class);
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
