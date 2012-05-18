/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.label;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.PreElement;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentConnector;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.terminal.gwt.client.ui.Connect.LoadStyle;
import com.vaadin.ui.Label;

@Connect(value = Label.class, loadStyle = LoadStyle.EAGER)
public class LabelConnector extends AbstractComponentConnector {

    @Override
    public LabelState getState() {
        return (LabelState) super.getState();
    }

    @Override
    protected void init() {
        super.init();
        getWidget().setConnection(getConnection());
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        boolean sinkOnloads = false;
        switch (getState().getContentMode()) {
        case PREFORMATTED:
            PreElement preElement = Document.get().createPreElement();
            preElement.setInnerText(getState().getText());
            // clear existing content
            getWidget().setHTML("");
            // add preformatted text to dom
            getWidget().getElement().appendChild(preElement);
            break;

        case TEXT:
            getWidget().setText(getState().getText());
            break;

        case XHTML:
        case RAW:
            sinkOnloads = true;
        case XML:
            getWidget().setHTML(getState().getText());
            break;
        default:
            getWidget().setText("");
            break;

        }
        if (sinkOnloads) {
            Util.sinkOnloadForImages(getWidget().getElement());
        }
    }

    @Override
    public VLabel getWidget() {
        return (VLabel) super.getWidget();
    }

}
