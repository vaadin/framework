package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.event.Action.Handler;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Window;

public class Ticket1857 extends Application implements Handler {

    public void init() {

        setTheme("tests-tickets");

        ExpandLayout el = new ExpandLayout();
        Window main = new Window("Testcase for #1857", el);
        setMainWindow(main);
        el.setMargin(true);
        el.setSpacing(true);

        final Table t = new Table();
        el.addComponent(t);
        el.expand(t);
        t.setSizeFull();
        addContentsToTable(t);
        t.setStyleName("foo");

        OrderedLayout footer = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        el.addComponent(footer);
        footer.setSpacing(true);

        final Button actionHandlerEnabler = new Button("Action handlers", false);
        footer.addComponent(actionHandlerEnabler);
        actionHandlerEnabler.setImmediate(true);
        actionHandlerEnabler.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                if (((Boolean) actionHandlerEnabler.getValue()).booleanValue()) {
                    t.addActionHandler(Ticket1857.this);
                } else {
                    t.removeActionHandler(Ticket1857.this);
                }
            }
        });

        final Button cellStylesEnabler = new Button("Cell styles", false);
        footer.addComponent(cellStylesEnabler);
        cellStylesEnabler.setImmediate(true);
        cellStylesEnabler.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                if (((Boolean) cellStylesEnabler.getValue()).booleanValue()) {
                    t.setCellStyleGenerator(new Table.CellStyleGenerator() {
                        public String getStyle(Object itemId, Object propertyId) {
                            Object cell = t.getContainerProperty(itemId,
                                    propertyId).getValue();
                            if (!(cell instanceof Integer)) {
                                return null;
                            }
                            int age = ((Integer) cell).intValue();
                            return age > 65 ? "old" : (age < 18 ? "young"
                                    : null);
                        }
                    });
                } else {
                    t.setCellStyleGenerator(null);
                }
            }
        });
        cellStylesEnabler.setValue(Boolean.TRUE);

    }

    private void addContentsToTable(Table t) {

        t.addContainerProperty("First name", String.class, "");
        t.addContainerProperty("Last name", String.class, "");
        t.addContainerProperty("Age", Integer.class, "");

        String firstNames[] = { "Quentin", "Marc", "Peter", "David", "Mary",
                "Jani", "Jane", "Brita" };
        String lastNames[] = { "Heiskanen", "Bjorn", "Torwalds", "Autere",
                "Smith", "Lindström" };

        for (int i = 0; i < 1000; i++) {
            t.addItem(new Object[] {
                    firstNames[((int) (Math.random() * firstNames.length))],
                    lastNames[((int) (Math.random() * lastNames.length))],
                    new Integer((int) (Math.random() * 100) + 10) },
                    new Integer(i));
        }
    }

    private final Action removeAction = new Action("Remove");

    public Action[] getActions(Object target, Object sender) {
        return new Action[] { removeAction };
    }

    public void handleAction(Action action, Object sender, Object target) {
        getMainWindow().showNotification("Removing row number:" + target);
        ((Table) sender).removeItem(target);
    }
}