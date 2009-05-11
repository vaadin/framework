package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.user.client.Element;

public interface SubPartAware {

    Element getSubPartElement(String subPart);

    String getSubPartName(Element subElement);

}