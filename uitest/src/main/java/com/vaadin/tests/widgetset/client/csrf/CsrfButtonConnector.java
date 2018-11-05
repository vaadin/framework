package com.vaadin.tests.widgetset.client.csrf;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.shared.GWT;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.VButton;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.client.MockApplicationConnection;
import com.vaadin.tests.widgetset.server.csrf.CsrfButton;

/**
 * Dummy connector to test our CSRF bug. See #14111.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
@Connect(CsrfButton.class)
public class CsrfButtonConnector extends AbstractComponentConnector {

    static Logger logger = Logger
            .getLogger(CsrfButtonConnector.class.getName());
    static {
        logger.setLevel(Level.ALL);
    }

    @Override
    public VButton getWidget() {
        return (VButton) super.getWidget();
    }

    @Override
    protected VButton createWidget() {
        return GWT.create(VButton.class);
    }

    public static final String ID = "CsrfButton";

    @Override
    public void init() {
        super.init();

        getWidget().getElement().setId(ID);
        getWidget().setText(csrfTokenInfo());
        getWidget()
                .addClickHandler(event -> getWidget().setText(csrfTokenInfo()));
    }

    private String csrfTokenInfo() {
        return getMockConnection().getMessageHandler().getCsrfToken() + ", "
                + getMockConnection().getLastCsrfTokenReceiver() + ", "
                + getMockConnection().getLastCsrfTokenSent();
    }

    private MockApplicationConnection getMockConnection() {
        return (MockApplicationConnection) getConnection();
    }

}
