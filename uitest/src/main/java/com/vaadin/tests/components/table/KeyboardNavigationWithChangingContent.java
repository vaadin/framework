package com.vaadin.tests.components.table;

import java.util.Arrays;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Sizeable;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class KeyboardNavigationWithChangingContent extends TestBase {

    @Override
    protected void setup() {
        ValueHolder<String> v1 = new ValueHolder<String>("test1");
        ValueHolder<String> v2 = new ValueHolder<String>("test2");
        ValueHolder<String> v3 = new ValueHolder<String>("test3");
        @SuppressWarnings("unchecked")
        final BeanItemContainer<ValueHolder<String>> bic = new BeanItemContainer<ValueHolder<String>>(
                Arrays.asList(v1, v2, v3));
        final Table t = new Table(null, bic);
        t.setSelectable(true);
        t.setMultiSelect(false);
        t.setWidth(200, Sizeable.UNITS_PIXELS);
        t.setHeight(100, Sizeable.UNITS_PIXELS);
        t.select(v1);
        t.focus();
        t.setMultiSelect(true);

        getLayout().addComponent(t);
        getLayout().addComponent(
                new Button("Change elements and selection",
                        new ClickListener() {

                            @Override
                            public void buttonClick(ClickEvent event) {

                                bic.removeAllItems();
                                ValueHolder<String> v4 = null;
                                for (int i = 4; i < 30; i++) {
                                    v4 = new ValueHolder<String>("test" + i);
                                    bic.addBean(v4);

                                }
                                t.select(t.firstItemId());
                                t.focus();
                            }
                        }));
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return "Table keyboard navigation does not work after the contents in table is changed";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return 5347;
    }

    public class ValueHolder<E> {
        private E value;

        public ValueHolder() {
        }

        public ValueHolder(E value) {
            this.value = value;
        }

        public void setValue(E value) {
            this.value = value;
        }

        public E getValue() {
            return value;
        }
    }

}
