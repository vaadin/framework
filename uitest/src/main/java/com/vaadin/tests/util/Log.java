package com.vaadin.tests.util;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class Log extends VerticalLayout {
    List<Label> eventLabels = new ArrayList<Label>();
    private boolean numberLogRows = true;
    private int nextLogNr = 1;

    public Log(int nr) {
        for (int i = 0; i < nr; i++) {
            Label l = createEventLabel();
            l.setId("Log_row_" + i);
            eventLabels.add(l);
            addComponent(l);
        }
        setId("Log");
        setCaption("Events:");
    }

    /**
     * Clears the rows and reset the row number to zero.
     */
    public Log clear() {
        for (Label l : eventLabels) {
            l.setValue("&nbsp;");
        }
        nextLogNr = 0;
        return this;
    }

    public Log log(String event) {
        int nr = eventLabels.size();
        for (int i = nr - 1; i > 0; i--) {
            eventLabels.get(i).setValue(eventLabels.get(i - 1).getValue());
        }
        String msg = event;
        if (numberLogRows) {
            msg = nextLogNr + ". " + msg;
            nextLogNr++;
        }
        eventLabels.get(0).setValue(msg);
        System.out.println(event);
        return this;
    }

    private Label createEventLabel() {
        Label l = new Label("&nbsp;", ContentMode.HTML);
        l.setWidth(null);
        return l;
    }

    public Log setNumberLogRows(boolean numberLogRows) {
        this.numberLogRows = numberLogRows;
        return this;
    }

}
