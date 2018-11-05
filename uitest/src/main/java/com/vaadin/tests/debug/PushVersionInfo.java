package com.vaadin.tests.debug;

import org.atmosphere.util.Version;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;

/**
 * Test UI for PUSH version string in debug window.
 *
 * @author Vaadin Ltd
 */
public class PushVersionInfo extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        if (request.getParameter("enablePush") != null) {
            getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
            Label label = new Label(Version.getRawVersion());
            label.addStyleName("atmosphere-version");
            addComponent(label);
        }
    }

    @Override
    protected String getTestDescription() {
        return "Debug window shows Push version in info Tab.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14904;
    }
}
