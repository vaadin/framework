package com.vaadin.themes.valoutil;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;

public class BodyStyleName implements EntryPoint {

    @Override
    public void onModuleLoad() {
        Document.get().getBody().addClassName("valo");
    }

}
