package com.vaadin.tests.components.textfield;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalSplitPanel;

public class OutOfSyncIssueWithKeyboardShortcut extends TestBase {

    public static class TestToppingsView extends CustomComponent {

        public static class Topping {
            private Long id;
            private String name = "ham";

            public void setId(Long id) {
                this.id = id;
            }

            public Long getId() {
                return id;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getName() {
                return name;
            }
        }

        private Table table = new Table();

        private Form form = new Form();

        private Map<Long, Topping> toppings = new HashMap<Long, Topping>();
        private long index = 1;

        private String previousFragment = null;

        public TestToppingsView() {
            setSizeFull();

            VerticalSplitPanel mainLayout = new VerticalSplitPanel();
            mainLayout.setSplitPosition(30);
            setCompositionRoot(mainLayout);

            table.setSizeFull();
            table.setImmediate(true);
            table.setSelectable(true);

            mainLayout.setFirstComponent(table);
            mainLayout.setSecondComponent(form);

            form.setImmediate(true);
            // this is critical for the problem to occur
            form.setBuffered(true);

            HorizontalLayout footer = new HorizontalLayout();
            footer.setSpacing(true);
            form.setFooter(footer);
            Button saveButton = new Button("Save");
            footer.addComponent(saveButton);

            // make saving the form the default action on Enter keypress
            saveButton.setClickShortcut(KeyCode.ENTER);

            table.addListener(new ValueChangeListener() {
                @Override
                public void valueChange(ValueChangeEvent event) {
                    Object value = event.getProperty().getValue();
                    if (value != null) {
                        String fragment = "edit/"
                                + String.valueOf(value)
                                        .replaceAll("[^0-9]", "");
                        if (!fragment.equals(previousFragment)) {
                            navigateTo(fragment);
                        }
                    }
                }
            });

            saveButton.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    form.commit();
                    Topping entity = getEntityForItem(form.getItemDataSource());
                    if (entity != null && entity.getId() == null) {
                        entity.setId(index++);
                        toppings.put(entity.getId(), entity);
                    }
                    refreshTable();
                    navigateTo(null);
                }
            });

            // create new entity at the beginning
            refreshTable();
            navigateTo("new");
        }

        public void navigateTo(String requestedDataId) {
            previousFragment = requestedDataId;

            if ("new".equals(requestedDataId)) {
                table.setValue(null);
                form.setVisible(true);
                setCurrentEntity(new Topping());
                form.focus();
            } else if (requestedDataId != null
                    && requestedDataId.startsWith("edit/")) {
                try {
                    Long id = Long.valueOf(requestedDataId.substring(5));
                    setCurrentEntity(getEntityForItem(table.getItem(id)));
                    form.focus();
                } catch (NumberFormatException e) {
                    setCurrentEntity(null);
                }
            } else {
                setCurrentEntity(null);
            }
        }

        private void refreshTable() {
            // refresh table
            BeanContainer<Long, Topping> container = new BeanContainer<Long, Topping>(
                    Topping.class);
            container.setBeanIdProperty("id");
            for (Topping entity : toppings.values()) {
                container.addBean(entity);
            }
            table.setContainerDataSource(container);
        }

        protected void setCurrentEntity(Topping entity) {
            form.setVisible(entity != null);
            if (entity != null) {
                Item item = table.getItem(entity.getId());
                if (item == null) {
                    item = new BeanItem<Topping>(entity);
                }
                form.setItemDataSource(item, Collections.singleton("name"));
            } else {
                form.setItemDataSource(null);
            }
        }

        public Topping getEntityForItem(Item item) {
            if (item != null) {
                return ((BeanItem<Topping>) item).getBean();
            } else {
                return null;
            }
        }

    }

    @Override
    protected void setup() {
        TestToppingsView testToppingsView = new TestToppingsView();
        addComponent(testToppingsView);
        getLayout().setSizeFull();
        getLayout().setExpandRatio(testToppingsView, 1);
    }

    @Override
    protected String getDescription() {
        return "Focus the text field and press ENTER.\n"
                + "Click on the table row to edit it, change the text to \"ahm\" using the keyboard and press ENTER again.\n"
                + "Then select the table row again.\n"
                + "This causes an Out of Sync error if the cursor position for the text field is sent too late to a component that is no longer in the layout.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6834;
    }
}
