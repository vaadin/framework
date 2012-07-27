/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.shared.AbstractFieldState;
import com.vaadin.terminal.gwt.client.ApplicationConnection;

public abstract class AbstractFieldConnector extends AbstractComponentConnector {

    @Override
    public AbstractFieldState getState() {
        return (AbstractFieldState) super.getState();
    }

    @Override
    public boolean isReadOnly() {
        return super.isReadOnly() || getState().isPropertyReadOnly();
    }

    public boolean isModified() {
        return getState().isModified();
    }

    /**
     * Checks whether the required indicator should be shown for the field.
     * 
     * Required indicators are hidden if the field or its data source is
     * read-only.
     * 
     * @return true if required indicator should be shown
     */
    public boolean isRequired() {
        return getState().isRequired() && !isReadOnly();
    }

    @Override
    protected void updateWidgetStyleNames() {
        super.updateWidgetStyleNames();

        // add / remove modified style name to Fields
        setWidgetStyleName(ApplicationConnection.MODIFIED_CLASSNAME,
                isModified());

        // add / remove error style name to Fields
        setWidgetStyleNameWithPrefix(getWidget().getStylePrimaryName(),
                ApplicationConnection.REQUIRED_CLASSNAME_EXT, isRequired());
    }
}
