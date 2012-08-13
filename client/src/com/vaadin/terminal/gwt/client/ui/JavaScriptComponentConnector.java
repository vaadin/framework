/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.JavaScriptComponentState;
import com.vaadin.terminal.gwt.client.JavaScriptConnectorHelper;
import com.vaadin.terminal.gwt.client.communication.HasJavaScriptConnectorHelper;
import com.vaadin.ui.AbstractJavaScriptComponent;

@Connect(AbstractJavaScriptComponent.class)
public final class JavaScriptComponentConnector extends
        AbstractComponentConnector implements HasJavaScriptConnectorHelper {

    private final JavaScriptConnectorHelper helper = new JavaScriptConnectorHelper(
            this) {
        @Override
        protected void showInitProblem(
                java.util.ArrayList<String> attemptedNames) {
            getWidget().showNoInitFound(attemptedNames);
        }
    };

    @Override
    public JavaScriptWidget getWidget() {
        return (JavaScriptWidget) super.getWidget();
    }

    @Override
    protected void init() {
        super.init();
        helper.init();
    }

    @Override
    public JavaScriptConnectorHelper getJavascriptConnectorHelper() {
        return helper;
    }

    @Override
    public JavaScriptComponentState getState() {
        return (JavaScriptComponentState) super.getState();
    }
}
