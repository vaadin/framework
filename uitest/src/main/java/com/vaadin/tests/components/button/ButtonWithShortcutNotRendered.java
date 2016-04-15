package com.vaadin.tests.components.button;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalSplitPanel;

@PreserveOnRefresh
@SuppressWarnings("deprecation")
public class ButtonWithShortcutNotRendered extends AbstractTestUI {

    @Override
    protected String getTestDescription() {
        return "Button with shortcut broken if the shortcut is rendered before the button";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9825;
    }

    Button defaultButton;
    Button otherButton;

    @SuppressWarnings("unchecked")
    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setWidth(100, Unit.PERCENTAGE);
        getLayout().setHeight(null);
        getLayout().setMargin(new MarginInfo(true, false, false, false));

        HierarchicalContainer container = new HierarchicalContainer();
        container.addContainerProperty("t1", String.class, "");
        container.addContainerProperty("t2", String.class, "");

        Item item = container.addItem("r1");
        item.getItemProperty("t1").setValue("Row1 t1");
        item.getItemProperty("t2").setValue("Row1 t2");

        item = container.addItem("r2");
        item.getItemProperty("t1").setValue("Row2 t1");
        item.getItemProperty("t2").setValue("Row2 t2");

        MyForm form = new MyForm();
        MyTable table = new MyTable(form, container);

        VerticalSplitPanel splitPanel = new VerticalSplitPanel();
        splitPanel.setWidth(100, Unit.PERCENTAGE);
        splitPanel.setHeight(400, Unit.PIXELS);
        splitPanel.setFirstComponent(table);
        splitPanel.setSecondComponent(form);
        splitPanel.setSplitPosition(50, Unit.PERCENTAGE);

        addComponent(splitPanel);
    }

    public class MyTable extends Table {
        final MyForm form;

        public MyTable(MyForm pform, HierarchicalContainer container) {
            form = pform;
            setContainerDataSource(container);
            setSelectable(true);
            setImmediate(true);
            setWidth(100, Unit.PERCENTAGE);
            setHeight(null);

            addValueChangeListener(new Property.ValueChangeListener() {

                @Override
                public void valueChange(
                        com.vaadin.data.Property.ValueChangeEvent event) {
                    final Item item = getItem(getValue());
                    form.setItemDataSource(item);
                }

            });
        }
    }

    public class MyForm extends Form implements ClickListener {
        final GridLayout layout;

        @SuppressWarnings("unchecked")
        public MyForm() {
            setWidth(100, Unit.PERCENTAGE);
            setHeight(null);
            setImmediate(true);

            setFormFieldFactory(new DefaultFieldFactory() {

                @Override
                public TextField createField(Item item, Object propertyId,
                        Component uiContext) {
                    TextField t = new TextField();
                    t.setWidth(100, Unit.PERCENTAGE);
                    t.setHeight(null);
                    t.setCaption((String) propertyId);
                    t.setImmediate(false);
                    return t;
                }
            });

            layout = new GridLayout(2, 1);
            layout.setWidth(100, Unit.PERCENTAGE);
            layout.setHeight(null);
            layout.setColumnExpandRatio(0, 0.5f);
            layout.setColumnExpandRatio(1, 0.5f);
            layout.setMargin(true);
            layout.setSpacing(true);
            setLayout(layout);

            HorizontalLayout footer = new HorizontalLayout();
            footer.setSpacing(true);
            footer.setMargin(false);
            footer.setVisible(false);
            setFooter(footer);

            defaultButton = new Button("Default Button", this);
            defaultButton.setClickShortcut(KeyCode.ENTER);
            footer.addComponent(defaultButton);

            otherButton = new Button("Other button", this);
            footer.addComponent(otherButton);
        }

        @Override
        public void setItemDataSource(Item newDataSource) {
            if (newDataSource != null) {
                super.setItemDataSource(newDataSource);
                layout.setVisible(true);
                getFooter().setVisible(true);
            } else {
                super.setItemDataSource(null);
                layout.setVisible(false);
                getFooter().setVisible(false);
            }
        }

        @Override
        @SuppressWarnings("rawtypes")
        protected void attachField(Object propertyId, Field field) {
            if (propertyId.equals("t1")) {
                layout.addComponent(field, 0, 0);
            } else if (propertyId.equals("t2")) {
                layout.addComponent(field, 1, 0);
            }
        }

        @Override
        public void buttonClick(ClickEvent event) {
            // NOP
        }

    }
}
