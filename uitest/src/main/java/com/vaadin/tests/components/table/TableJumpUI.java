package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;

/**
 * Test for ensuring page doesn't jump up to the Table selection on IE with
 * these steps:
 *
 * <p>
 * 1. refresh page <br>
 * 2. click within URL bar <br>
 * 3. click a table row to select it <br>
 * 4. click within one of the text fields <br>
 * 5. scroll down <br>
 * 6. click the button
 * </p>
 * The problem is that IE for some reason does not fire a blur event for the
 * table at step 4, leading to table thinking it is focused when it is updated
 * in step 6.
 *
 * @author Vaadin Ltd
 */
public class TableJumpUI extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {

        BeanItemContainer<TestObj> container = new BeanItemContainer<>(
                TestObj.class);
        for (int i = 0; i < 2; i++) {
            container.addBean(new TestObj(i));
        }

        final Table table = new Table();
        table.setPageLength(2);
        table.setContainerDataSource(container);
        table.setSelectable(true);
        addComponent(table);

        // After the table we have a lot of textfields so that we have to scroll
        // down to the button
        for (int i = 0; i < 40; i++) {
            TextField tf = new TextField();
            tf.setValue(String.valueOf(i));
            final int j = i;
            tf.addFocusListener(event -> log("Tf " + j + " focus"));
            tf.addBlurListener(event -> log("Tf " + j + " Blur"));
            addComponent(tf);
        }

        addButton("refresh row cache", event -> table.refreshRowCache());
    }

    @Override
    protected String getTestDescription() {
        return "Page shouldn't scroll up to Table selection when the button is clicked.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 19676;
    }

    public static class TestObj {
        int i;
        String text;

        public TestObj(final int i) {
            this.i = i;
            text = "Object " + i;
        }

        public int getI() {
            return i;
        }

        public void setI(final int i) {
            this.i = i;
        }

        public String getText() {
            return text;
        }

        public void setText(final String text) {
            this.text = text;
        }

    }

}
