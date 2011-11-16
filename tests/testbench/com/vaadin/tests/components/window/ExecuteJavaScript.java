package com.vaadin.tests.components.window;

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Root;

public class ExecuteJavaScript extends AbstractTestCase {

    @Override
    public void init() {
        final Root mainWindow = new Root("Test");
        setMainWindow(mainWindow);

        for (final String script : new String[] { "alert('foo');",
                "window.print()", "document.write('foo')" }) {
            Panel p = new Panel("Example: " + script);
            p.addComponent(createScriptButton(script));
            mainWindow.addComponent(p);
        }

    }

    private Button createScriptButton(final String script) {
        Button b = new Button(script);
        b.addListener(new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                getMainWindow().executeJavaScript(script);
            }
        });

        return b;
    }

    @Override
    protected String getDescription() {
        return "Test for the Window.executeJavaScript method. Click a button to execute the javascript";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3589;
    }
}
