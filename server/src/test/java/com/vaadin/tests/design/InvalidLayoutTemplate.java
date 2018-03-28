package com.vaadin.tests.design;

import com.vaadin.ui.Button;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 *
 * @since
 * @author Vaadin Ltd
 */
public class InvalidLayoutTemplate extends VerticalLayout {
    private NativeButton firstButton;
    private NativeButton secondButton;
    private NativeButton yetanotherbutton; // generated based on caption
    private Button clickme; // generated based on caption
    private TextField shouldNotBeMapped;

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

    public TextField getShouldNotBeMapped() {
        return shouldNotBeMapped;
    }

}
