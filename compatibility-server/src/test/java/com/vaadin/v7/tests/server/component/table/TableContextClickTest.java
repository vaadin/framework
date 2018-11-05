package com.vaadin.v7.tests.server.component.table;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.ContextClickEvent.ContextClickListener;
import com.vaadin.v7.shared.ui.table.TableConstants.Section;
import com.vaadin.v7.ui.Table;

public class TableContextClickTest extends Table {

    private String error = null;
    private boolean handled = false;

    @Test
    public void testContextClickListenerWithTableEvent() {
        addContextClickListener(new ContextClickListener() {

            @Override
            public void contextClick(ContextClickEvent event) {
                if (!(event instanceof TableContextClickEvent)) {
                    return;
                }

                TableContextClickEvent e = (TableContextClickEvent) event;
                if (e.getSection() != Section.BODY) {
                    error = "Event section was not BODY.";
                }
                handled = true;
            }
        });
        fireEvent(new TableContextClickEvent(this, null, null, null,
                Section.BODY));

        if (error != null) {
            fail(error);
        } else if (!handled) {
            fail("Event was not handled by the ContextClickListener");
        }
    }
}
