/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import com.vaadin.terminal.gwt.client.ui.VDateFieldCalendar;
import com.vaadin.terminal.gwt.client.ui.VPasswordField;
import com.vaadin.terminal.gwt.client.ui.VSplitPanelVertical;
import com.vaadin.terminal.gwt.client.ui.VTextArea;
import com.vaadin.terminal.gwt.client.ui.VWindow;

public abstract class WidgetMap {

    public Paintable instantiate(Class<? extends Paintable> classType) {

        /*
         * Yes, this (including the generated) may look very odd code, but due
         * the nature of GWT, we cannot do this with reflect. Luckily this is
         * mostly written by WidgetSetGenerator, here are just some hacks. Extra
         * instantiation code is needed if client side widget has no "native"
         * counterpart on client side.
         */
        if (VSplitPanelVertical.class == classType) {
            return new VSplitPanelVertical();
        } else if (VTextArea.class == classType) {
            return new VTextArea();

        } else if (VDateFieldCalendar.class == classType) {
            return new VDateFieldCalendar();
        } else if (VPasswordField.class == classType) {
            return new VPasswordField();
        } else if (VWindow.class == classType) {
            return new VWindow();
        } else {
            return null; // let generated type handle this
        }
    }

    public abstract Class<? extends Paintable> getImplementationByServerSideClassName(
            String fullyqualifiedName,
            ApplicationConfiguration applicationConfiguration);

}
