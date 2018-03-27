package com.vaadin.tests.server.component.table;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.ContextClickEvent.ContextClickListener;
import com.vaadin.shared.ui.table.TableConstants.Section;
import com.vaadin.ui.Table;

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
            Assert.fail(error);
        } else if (!handled) {
            Assert.fail("Event was not handled by the ContextClickListener");
        }
    }
}
