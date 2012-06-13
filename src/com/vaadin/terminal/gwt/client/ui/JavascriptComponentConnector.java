/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;
import com.vaadin.terminal.gwt.client.JavascriptConnectorHelper;
import com.vaadin.terminal.gwt.client.communication.HasJavascriptConnectorHelper;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.ui.AbstractJavascriptComponent;

@Connect(AbstractJavascriptComponent.class)
public class JavascriptComponentConnector extends AbstractComponentConnector
        implements HasJavascriptConnectorHelper {

    private final JavascriptConnectorHelper helper = new JavascriptConnectorHelper(
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

    @Override
    protected void init() {
        helper.init();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        helper.fireNativeStateChange();
    }

    private static native void addGetWidgetElement(
            JavaScriptObject connectorWrapper, Element element)
    /*-{
        connectorWrapper.getWidgetElement = function() {
            return element;
        };
    }-*/;

    @Override
    public JavascriptWidget getWidget() {
        return (JavascriptWidget) super.getWidget();
    }

    public JavascriptConnectorHelper getJavascriptConnectorHelper() {
        return helper;
    }
}
