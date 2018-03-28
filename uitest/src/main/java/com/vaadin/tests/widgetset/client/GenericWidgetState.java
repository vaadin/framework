package com.vaadin.tests.widgetset.client;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.annotations.DelegateToWidget;

public class GenericWidgetState extends AbstractComponentState {
    @DelegateToWidget
    public String genericText;
}
