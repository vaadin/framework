package com.vaadin.tests.minitutorials.v7a2;

import com.google.gwt.dom.client.Element;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.ui.layout.ElementResizeEvent;
import com.vaadin.client.ui.layout.ElementResizeListener;

/**
 * Mini tutorial code for
 * https://vaadin.com/wiki/-/wiki/Main/Widget%20styling%20using%20only%20CSS,
 * https
 * ://vaadin.com/wiki/-/wiki/Main/Lightweight%20calculations%20of%20widget%20l
 * ayout and https://vaadin.com/wiki/-/wiki/Main/Complex%20widget%20layouts
 *
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class MyPickerConnector extends AbstractComponentConnector
        implements SimpleManagedLayout {
    @Override
    public MyPickerWidget getWidget() {
        return (MyPickerWidget) super.getWidget();
    }

    private final ElementResizeListener listener = new ElementResizeListener() {
        @Override
        public void onElementResize(ElementResizeEvent e) {
            int buttonWidth = getLayoutManager().getOuterWidth(e.getElement());
            buttonWidth -= getLayoutManager().getMarginRight(e.getElement());
            getWidget().adjustButtonSpace(buttonWidth);
        }
    };

    @Override
    protected void init() {
        Element button = getWidget().getWidget(1).getElement();
        getLayoutManager().addElementResizeListener(button, listener);

        getLayoutManager().registerDependency(this, button);
    }

    @Override
    public void onUnregister() {
        Element button = getWidget().getWidget(1).getElement();
        getLayoutManager().removeElementResizeListener(button, listener);

        getLayoutManager().unregisterDependency(this, button);
    }

    @Override
    public void layout() {
        Element button = getWidget().getWidget(1).getElement();
        int buttonWidth = getLayoutManager().getOuterWidth(button);
        buttonWidth -= getLayoutManager().getMarginRight(button);
        getWidget().adjustButtonSpace(buttonWidth);
    }
}
