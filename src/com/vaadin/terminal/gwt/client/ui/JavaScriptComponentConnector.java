/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;
import com.vaadin.terminal.gwt.client.JavaScriptConnectorHelper;
import com.vaadin.terminal.gwt.client.communication.HasJavaScriptConnectorHelper;
import com.vaadin.ui.AbstractJavaScriptComponent;

@Connect(AbstractJavaScriptComponent.class)
public class JavaScriptComponentConnector extends AbstractComponentConnector
        implements HasJavaScriptConnectorHelper {

    private final JavaScriptConnectorHelper helper = new JavaScriptConnectorHelper(
            this) {
        @Override
        protected void showInitProblem(
                java.util.ArrayList<String> attemptedNames) {
            getWidget().showNoInitFound(attemptedNames);
        }

        @Override
        protected JavaScriptObject createConnectorWrapper() {
            JavaScriptObject connectorWrapper = super.createConnectorWrapper();
            addGetWidgetElement(connectorWrapper, getWidget().getElement());
            return connectorWrapper;
        }
    };

    private static native void addGetWidgetElement(
            JavaScriptObject connectorWrapper, Element element)
    /*-{
        connectorWrapper.getWidgetElement = function() {
            return element;
        };
    }-*/;

    @Override
    public JavaScriptWidget getWidget() {
        return (JavaScriptWidget) super.getWidget();
    }

    @Override
    protected void init() {
        super.init();
        helper.init();
    }

    public JavaScriptConnectorHelper getJavascriptConnectorHelper() {
        return helper;
    }

    @Override
    public JavaScriptComponentState getState() {
        return (JavaScriptComponentState) super.getState();
    }
}
