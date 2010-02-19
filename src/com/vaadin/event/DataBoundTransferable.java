package com.vaadin.event;

import java.util.Map;

import com.vaadin.ui.Component;

public abstract class DataBoundTransferable extends TransferableImpl {

    public DataBoundTransferable(Component sourceComponent,
            Map<String, Object> rawVariables) {
        super(sourceComponent, rawVariables);
    }

    public abstract Object getItemId();

    public abstract Object getPropertyId();

}
