package com.vaadin.tests.components.abstractfield;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextArea;

public class ShortcutAddAndRemove extends TestBase {

    private Log log;
    private TextArea textArea;

    @Override
    protected void setup() {
        log = new Log(4);

        final Button logButton = new Button("Log a row (enter shortcut)");
        logButton.setClickShortcut(KeyCode.ENTER);
        logButton.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                log.log("Log button was clicked");
            }
        });

        final Button removeShortcut = new Button("Remove shortcut");
        removeShortcut.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                logButton.removeClickShortcut();
                logButton.setCaption("Log a row (no shortcut)");
            }
        });
        final Button addShortcut = new Button("Add shortcut");
        addShortcut.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                logButton.setClickShortcut(KeyCode.ENTER);
                logButton.setCaption("Log a row (enter shortcut)");
            }
        });
        addComponent(log);
        addComponent(logButton);
        textArea = new TextArea("Enter key does not break lines ...");
        textArea.setRows(5);
        textArea.setColumns(20);
        addComponent(textArea);
        addComponent(removeShortcut);
        addComponent(addShortcut);

    }

    @Override
    protected String getDescription() {
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
