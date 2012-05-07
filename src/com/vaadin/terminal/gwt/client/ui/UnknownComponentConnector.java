/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;


public class UnknownComponentConnector extends AbstractComponentConnector {

    @Override
    public boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public VUnknownComponent getWidget() {
        return (VUnknownComponent) super.getWidget();
    }

    public void setServerSideClassName(String serverClassName) {
        getWidget()
                .setCaption(
                        "Widgetset does not contain implementation for "
                                + serverClassName
                                + ". Check its component connector's @Connect mapping, widgetsets "
                                + "GWT module description file and re-compile your"
                                + " widgetset. In case you have downloaded a vaadin"
                                + " add-on package, you might want to refer to "
                                + "<a href='http://vaadin.com/using-addons'>add-on "
                                + "instructions</a>.");
    }
}
