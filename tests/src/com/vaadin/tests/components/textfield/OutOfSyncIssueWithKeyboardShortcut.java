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
            // TODO this is critical for the problem to occur
            form.setWriteThrough(false);

            HorizontalLayout footer = new HorizontalLayout();
            footer.setSpacing(true);
            form.setFooter(footer);
            Button saveButton = new Button("Save");
            footer.addComponent(saveButton);

            // make saving the form the default action on Enter keypress
            saveButton.setClickShortcut(KeyCode.ENTER);

            table.addListener(new ValueChangeListener() {
                public void valueChange(ValueChangeEvent event) {
                    Object value = event.getProperty().getValue();
                    if (value != null) {
                        String fragment = "edit/"
                                + String.valueOf(value)
                                        .replaceAll("[^0-9]", "");
                        if (!fragment.equals(previousFragment)) {
                            navigateTo(fragment);
                            previousFragment = fragment;
                        }
                    }
                }
            });

            saveButton.addListener(new ClickListener() {
                public void buttonClick(ClickEvent event) {
                    form.commit();
                    Topping entity = getEntityForItem(form.getItemDataSource());
                    if (entity != null && entity.getId() == null) {
                        entity.setId(index++);
                        toppings.put(entity.getId(), entity);
                    }
                    navigateTo(null);
                }
            });

            // create new entity at the beginning
            navigateTo("new");
        }

        public void navigateTo(String requestedDataId) {
            // refresh table
            BeanContainer<Long, Topping> container = new BeanContainer<Long, Topping>(
                    Topping.class);
            container.setBeanIdProperty("id");
            for (Topping entity : toppings.values()) {
                container.addBean(entity);
            }
            table.setContainerDataSource(container);

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
        return "Open the toppings view and create a new topping \"ham\".\n"
                + "Select the topping in the table to edit it (might require two clicks due to #6833).\n"
                + "Edit the name to \"ahm\" and press ENTER without first moving the focus out of the field. This triggers the Save button.\n"
                + "Deselect the row in the table (needed because of #6833).\n"
                + "An out of sync error is displayed, as there is a variable change for the text field that is no longer on the screen.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6834;
    }
}
