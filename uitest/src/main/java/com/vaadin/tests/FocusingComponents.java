package com.vaadin.tests;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;

/**
 * Simple test helper to test Focusable.focus() method.
 * 
 */
public class FocusingComponents extends CustomComponent {
    GridLayout lo = new GridLayout(2, 1);

    public FocusingComponents() {

        setCompositionRoot(lo);
        lo.setSpacing(true);

        Focusable f;

        f = new Button();

        addFocusableTest(f);
        addFocusableTest(new ComboBox());
        addFocusableTest(new TextField());
        addFocusableTest(new DateField());
        addFocusableTest(new NativeSelect());
        addFocusableTest(new ListSelect());
        addFocusableTest(new OptionGroup());
        OptionGroup optionGroup = new OptionGroup();
        optionGroup.setMultiSelect(true);
        addFocusableTest(optionGroup);

    }

    private void addFocusableTest(final Focusable f) {

        f.setCaption(f.getClass().getSimpleName());
        lo.addComponent(f);

        if (f instanceof AbstractSelect) {
            AbstractSelect s = (AbstractSelect) f;
            s.addItem("Foo");
            s.addItem("Bar");
        }

        Button focus = new Button("focus");
        focus.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                f.focus();
            }
        });
        lo.addComponent(focus);

    }

}
