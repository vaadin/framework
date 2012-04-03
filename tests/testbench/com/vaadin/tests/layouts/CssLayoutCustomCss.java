package com.vaadin.tests.layouts;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.NativeButton;

public class CssLayoutCustomCss extends TestBase implements ClickListener {

    protected Map<Component, String> css = new HashMap<Component, String>();
    private CssLayout layout;

    @Override
    protected void setup() {
        setTheme("tests-tickets");
        layout = new CssLayout() {
            @Override
            protected String getCss(com.vaadin.ui.Component c) {
                return css.get(c);
            }
        };
        layout.setSizeFull();
        addComponent(layout);

        layout.addComponent(createButton("color:red"));
        layout.addComponent(createButton("color: blue"));
        layout.addComponent(createButton("color: green"));
    }

    private Component createButton(String string) {
        NativeButton button = new NativeButton(string);
        css.put(button, string);
        button.addListener(this);
        return button;
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    public void buttonClick(ClickEvent event) {
        Button b = event.getButton();
        if (b.getCaption().contains("not ")) {
            b.setCaption(b.getCaption().substring(4));
            css.put(b, b.getCaption());
        } else {
            css.remove(b);
            b.setCaption("not " + b.getCaption());
        }
        layout.requestRepaint();

    }

}
