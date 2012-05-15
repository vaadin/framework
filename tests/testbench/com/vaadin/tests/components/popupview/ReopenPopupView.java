/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.tests.components.popupview;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.PopupView;

public class ReopenPopupView extends AbstractTestRoot {
    private final Log log = new Log(5);

    @Override
    protected void setup(WrappedRequest request) {
        addComponent(log);
        addComponent(new PopupView("PopupView", new Button("Button",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        log.log("Button clicked");
                    }
                })));
    }

    @Override
    protected String getTestDescription() {
        return "Clicking a button in a PopupView should work every time";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8804);
    }

}
