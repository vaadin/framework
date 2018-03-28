package com.vaadin.tests.components.checkbox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.checkbox.CheckBoxServerRpc;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;

public class CheckBoxRpcCount extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Label countLabel = new Label("No RPC calls made yet.");
        countLabel.setId("count-label");
        addComponent(countLabel);

        CheckBox cb = new CheckBox("Click me to start counting...") {
            {
                // Register a new RPC that counts the number of invocations.
                registerRpc(new CheckBoxServerRpc() {
                    private int rpcCount = 0;

                    @Override
                    public void setChecked(boolean checked,
                            MouseEventDetails mouseEventDetails) {
                        rpcCount++;
                        countLabel.setValue(rpcCount + " RPC call(s) made.");
                    }

                });
            }
        };
        addComponent(cb);
    }

    @Override
    protected String getTestDescription() {
        return "Test for verifying that no extra RPC calls are made when clicking on CheckBox label.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8259;
    }

}
