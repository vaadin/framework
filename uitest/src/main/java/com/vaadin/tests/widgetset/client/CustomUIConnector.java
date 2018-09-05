package com.vaadin.tests.widgetset.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.vaadin.client.ui.ui.UIConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.ui.UI;

@Connect(UI.class)
public class CustomUIConnector extends UIConnector {
    @Override
    protected void init() {
        super.init();
        registerRpc(CustomUIConnectorRpc.class, () -> {
            SpanElement span = Document.get().createSpanElement();
            span.setInnerText("This is the "
                    + CustomUIConnector.this.getClass().getSimpleName());
            Document.get().getBody().insertFirst(span);
        });
    }
}
