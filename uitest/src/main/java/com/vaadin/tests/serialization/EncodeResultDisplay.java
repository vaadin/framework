package com.vaadin.tests.serialization;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.client.EncoderResultDisplayConnector;

@Widgetset(TestingWidgetSet.NAME)
public class EncodeResultDisplay extends AbstractTestUIWithLog {

    public static class EncoderResultDisplayExtension
            extends AbstractExtension {
        public EncoderResultDisplayExtension(
                EncoderResultDisplayConnector.ReportRpc rpc) {
            registerRpc(rpc);
        }

        public void extend(EncodeResultDisplay target) {
            super.extend(target);
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        log.setNumberLogRows(false);
        new EncoderResultDisplayExtension(
                (name, encodedValue) -> log(name + ": " + encodedValue))
                        .extend(this);
    }

    @Override
    protected int getLogSize() {
        return 15;
    }
}
