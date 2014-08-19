package com.vaadin.tests.components.table;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

public class TextFieldRelativeWidth extends AbstractTestUI {

    @Override
    public void setup(VaadinRequest request) {
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

        private Button addButton = new Button("Add new row", this);

        private String inputPrompt;
        private String inputPromptChild;
        private int nextItemIndex = 1;

        @SuppressWarnings("unchecked")
        public EditTable() {
            setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
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
        }

        @SuppressWarnings("unchecked")
        public void addNewRow() {
            IndexedContainer idc = (IndexedContainer) getContainerDataSource();
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
            tf.setWidth("100%");
            tf.addStyleName("childtf");
            newItem.getItemProperty("text").setValue(tf);

        }

        public void setButtonCaption(String caption) {
            addButton.setCaption(caption);
        }

        @Override
        public void buttonClick(ClickEvent event) {
            Button b = event.getButton();
            if (b == addButton) {
                select(getNullSelectionItemId());
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
    protected String getTestDescription() {
        return "The table has 3 columns. The second column is expanded and contains 100% wide textfields. These should fill the available space. The third column is empty.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3145;
    }
}
