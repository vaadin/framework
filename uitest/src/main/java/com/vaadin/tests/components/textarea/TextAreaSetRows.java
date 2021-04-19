package com.vaadin.tests.components.textarea;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;

public class TextAreaSetRows extends AbstractTestUI {

    protected static final String ROWS_0 = "Set rows to 0";
    protected static final String ROWS_1 = "Set rows to 1";
    protected static final String ROWS_2 = "Set rows to 2";
    protected static final String ROWS_3 = "Set rows to 3";
    protected static final String ROWS_4 = "Set rows to 4";
    protected static final String HEIGHT0 = "Set height to 0px";
    protected static final String HEIGHTR = "Reset height setting";
    protected static final String WWRAP = "Toggle word wrap";
    protected static final String LONGS = "Use longer contents (separate)";
    protected static final String LONGN = "Use longer contents (no breaks)";
    protected static final String SCROLLB = "Add scrollbar to panel";

    @Override
    protected void setup(VaadinRequest request) {
        TextArea ta = new TextArea();
        String value = "0\n1\n2\n3\n4\n5\n6\n7\n8\n9\n";
        ta.setValue(value);
        addComponent(ta);

        addComponent(new Button(ROWS_0, e -> ta.setRows(0)));
        addComponent(new Button(ROWS_1, e -> ta.setRows(1)));
        addComponent(new Button(ROWS_2, e -> ta.setRows(2)));
        addComponent(new Button(ROWS_3, e -> ta.setRows(3)));
        addComponent(new Button(ROWS_4, e -> ta.setRows(4)));
        addComponent(new Button(HEIGHT0, e -> ta.setHeight("0px")));
        addComponent(new Button(HEIGHTR, e -> ta.setHeight("-1px")));
        addComponent(new Button(WWRAP, e -> ta.setWordWrap(!ta.isWordWrap())));
        addComponent(new Button(LONGS,
                e -> ta.setValue(value + LoremIpsum.get(50))));
        addComponent(new Button(LONGN,
                e -> ta.setValue(value + getClass().getName())));

        Panel p = new Panel();
        CssLayout content = new CssLayout();
        p.setContent(content);
        content.setHeight("0px");
        p.setHeightUndefined();
        p.setWidth("100px");
        addComponent(p);
        addComponent(new Button(SCROLLB, e -> content.setWidth("200px")));
    }

    @Override
    protected Integer getTicketNumber() {
        return 10138;
    }

    @Override
    protected String getTestDescription() {
        return "Default height: 5 rows. Minimum height: 1 rows. "
                + "Height should update as expected. Disabling word wrap "
                + "adds space for a scrollbar whether one is needed or not. "
                + "Firefox always behaves like word wrap was disabled.";
    }
}
