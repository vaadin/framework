package com.vaadin.tests.components.button;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;

public class ButtonWithShortcutNotRendered extends Application {

    Button defaultButton;
    Button otherButton;

    @Override
    public void init() {
        Window mainWindow = new Window("Vaadin Test Application",
                new VerticalLayout());
        mainWindow.getContent().setSizeFull();
        mainWindow.setSizeFull();
        setMainWindow(mainWindow);

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
        splitPanel.setFirstComponent(table);
        splitPanel.setSecondComponent(form);
        splitPanel.setSplitPosition(50, HorizontalSplitPanel.UNITS_PERCENTAGE);

        mainWindow.addComponent(splitPanel);
    }

    public class MyTable extends Table {
        final MyForm form;

        public MyTable(MyForm pform, HierarchicalContainer container) {
            form = pform;
            setContainerDataSource(container);
            setSelectable(true);
            setImmediate(true);
            setSizeFull();

            addListener(new Property.ValueChangeListener() {

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

        public MyForm() {
            setWidth(100, UNITS_PERCENTAGE);
            setImmediate(true);

            setFormFieldFactory(new DefaultFieldFactory() {

                @Override
                public Field createField(Item item, Object propertyId,
                        Component uiContext) {
                    TextField t = new TextField();
                    t.setWidth(100, Form.UNITS_PERCENTAGE);
                    t.setCaption((String) propertyId);
                    t.setImmediate(false);
                    return t;
                }
            });

            layout = new GridLayout(2, 1);
            layout.setWidth(100, UNITS_PERCENTAGE);
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

            defaultButton = new Button("Default Button", (ClickListener) this);
            defaultButton.setClickShortcut(KeyCode.ENTER);
            footer.addComponent(defaultButton);

            otherButton = new Button("Other button", (ClickListener) this);
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
        protected void attachField(Object propertyId, Field field) {
            if (propertyId.equals("t1")) {
                layout.addComponent(field, 0, 0);
            } else if (propertyId.equals("t2")) {
                layout.addComponent(field, 1, 0);
            }
        }

        public void buttonClick(ClickEvent event) {
            // TODO Auto-generated method stub
            if (event.getButton() == defaultButton) {
                getWindow().showNotification("Default button clicked");
            }
        }

    }
}
