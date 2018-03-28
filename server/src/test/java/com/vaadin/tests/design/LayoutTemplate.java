package com.vaadin.tests.design;

import com.vaadin.ui.Button;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;

/**
 * Template to be populated in the tests
 *
 * @since
 * @author Vaadin Ltd
 */
public class LayoutTemplate extends VerticalLayout {
    private NativeButton firstButton; // assigned based on local id
    private NativeButton secondButton; // assigned based on id
    private NativeButton yetanotherbutton; // assigned based on caption
    private Button clickme; // assigned based on caption

    public NativeButton getFirstButton() {
        return firstButton;
    }

    public NativeButton getSecondButton() {
        return secondButton;
    }

    public NativeButton getYetanotherbutton() {
        return yetanotherbutton;
    }

    public Button getClickme() {
        return clickme;
    }
}
