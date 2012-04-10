package com.vaadin.tests.components.tabsheet;

import java.util.ArrayList;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class TabKeyboardNavigation extends TestBase {

    int index = 1;
    ArrayList<Component> tabs = new ArrayList<Component>();
    TabSheet ts = new TabSheet();
    Log focusblur = new Log(10);

    @Override
    protected void setup() {
        ts.setWidth("500px");
        ts.setHeight("500px");

        ts.addListener(new FocusListener() {
            public void focus(FocusEvent event) {
                focusblur.log("Tabsheet focused!");
            }
        });

        ts.addListener(new BlurListener() {
            public void blur(BlurEvent event) {
                focusblur.log("Tabsheet blurred!");
            }
        });

        for (int i = 0; i < 5; ++i) {
            addTab();
        }

        Button addTab = new Button("Add a tab", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                addTab();
            }
        });
        Button focus = new Button("Focus tabsheet", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                ts.focus();
            }
        });

        addComponent(addTab);
        addComponent(focus);

        TextField tf = new TextField();
        addComponent(tf);
        addComponent(focusblur);
        addComponent(ts);
        tf = new TextField();
        addComponent(tf);
    }

    @Override
    protected String getDescription() {
        return "The tab bar should be focusable and arrow keys should switch tabs. The del key should close a tab if closable.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5100;
    }

    private Tab addTab() {
        Layout content = new VerticalLayout();
        tabs.add(content);
        content.addComponent(new Label("Tab " + index));
        content.addComponent(new TextField());
        Tab tab = ts.addTab(content, "Tab " + index, null);
        if (index == 2) {
            tab.setClosable(true);
        }
        if (index == 4) {
            tab.setEnabled(false);
        }
        index++;
        return tab;
    }
}
