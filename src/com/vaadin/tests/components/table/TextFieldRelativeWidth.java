package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;

public class TextFieldRelativeWidth extends TestBase {

    @Override
    public void setup() {
        TextField tf = new TextField("test", "testing");
        tf.setWidth("100%");

        EditTable t = new EditTable();
        t.setButtonCaption("Click to add new Key Research Question");
        t.setInputPrompt("Key Reseach question");
        t.setInputPromptChild("Question details");
        t.addNewRow();
        addComponent(t);
    }

    public class EditTable extends Table implements Button.ClickListener {

        private Button addButton = new Button("Add new row",
                (Button.ClickListener) this);

        private String inputPrompt;

        private String inputPromptChild;

        private int nextItemIndex = 1;

        private static final long serialVersionUID = 3326806911297977454L;

        public EditTable() {
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
            inputPrompt = "";
            setPageLength(100);
            setHeight("100%");
            setSizeFull();
            addContainerProperty("id", Integer.class, null);
            addContainerProperty("text", Component.class, null);
            addContainerProperty("button", Button.class, null);
            setColumnExpandRatio("text", 1);
            Item i = getItem(addItem());
            i.getItemProperty("text").setValue(addButton);
            setImmediate(true);
            setSelectable(true);
            addListener(new Property.ValueChangeListener() {
                private static final long serialVersionUID = 448896474865195605L;

                public void valueChange(
                        com.vaadin.data.Property.ValueChangeEvent event) {
                    IndexedContainer idc = (IndexedContainer) getContainerDataSource();

                }

            });
        }

        public void addNewRow() {
            IndexedContainer idc = (IndexedContainer) this
                    .getContainerDataSource();
            int size = idc.size();
            Object itemId = idc.addItemAt(size - 1);
            Item newItem = idc.getItem(itemId);
            TextField tf = new TextField();
            if (inputPrompt != null && inputPrompt.length() > 0) {
                tf.setInputPrompt(inputPrompt);
            }
            tf.setWidth("100%");

            newItem.getItemProperty("id").setValue(nextItemIndex);
            nextItemIndex++;
            newItem.getItemProperty("text").setValue(tf);
            setValue(itemId);
            itemId = idc.addItemAt(size);
            newItem = idc.getItem(itemId);

            tf = new TextField();
            if (inputPromptChild != null && inputPromptChild.length() > 0) {
                tf.setInputPrompt(inputPromptChild);
            }
            // tf.setRows(1);
            // tf.setHeight("45px");
            tf.setWidth("100%");
            tf.addStyleName("childtf");
            newItem.getItemProperty("text").setValue(tf);

        }

        public void setButtonCaption(String caption) {
            addButton.setCaption(caption);
        }

        public void buttonClick(ClickEvent event) {
            Button b = event.getButton();
            if (b == addButton) {
                this.select(getNullSelectionItemId());
                addNewRow();
            }
        }

        public void setInputPrompt(String string) {
            inputPrompt = string;
        }

        public void setInputPromptChild(String string) {
            inputPromptChild = string;
        }

    }

    @Override
    protected String getDescription() {
        return "The table has 3 columns. The second column is expanded and contains 100% wide textfields. These should fill the available space. The third column is empty.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3145;
    }
}
